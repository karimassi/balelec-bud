package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.VisibleForTesting;
import androidx.arch.core.util.Function;
import androidx.room.Room;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDAO;
import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDatabase;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ConcertFlow implements ConcertFlowInterface {
    private List<NotificationSchedulerInterface> schedulers = new LinkedList<>();
    private ConcertOfInterestDAO concertOfInterestDAO;
    private Context context;
    private ConcertOfInterestDatabase db;

    public ConcertFlow(Context context) {
        this.context = context;
        this.db = Room.databaseBuilder(context,
                ConcertOfInterestDatabase.class, "ConcertsOfInterest")
                .build();
        this.concertOfInterestDAO = this.db.getConcertOfInterestDAO();
    }

    @VisibleForTesting
    public void setDb(ConcertOfInterestDatabase concertDb) {
        this.db.close();
        this.concertOfInterestDAO = concertDb.getConcertOfInterestDAO();
    }

    @Override
    public void addNotificationScheduler(NotificationSchedulerInterface scheduler) {
        schedulers.add(scheduler);
    }

    @Override
    public void getAllScheduledConcert(final Function<List<Slot>, Void> callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final List<Slot> res = concertOfInterestDAO.getAllConcertOfInterest();
                callback.apply(res);
            }
        });
    }

    @Override
    public void scheduleNewConcert(final Slot newSlot) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (!concertOfInterestDAO.getAllConcertOfInterest().contains(newSlot)) {
                    concertOfInterestDAO.insertConcert(newSlot);
                }
            }
        });
        for (NotificationSchedulerInterface notificationScheduler : ConcertFlow.this.schedulers)
            notificationScheduler.scheduleNotification(ConcertFlow.this.context, newSlot);
    }

    @Override
    public void removeConcert(final Slot slot) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                concertOfInterestDAO.removeConcert(slot);
            }
        });
        for (NotificationSchedulerInterface notificationScheduler : this.schedulers)
            notificationScheduler.cancelNotification(this.context, slot);
    }

    @Override
    public void close() {
        this.db.close();
    }

    @VisibleForTesting
    void clearNotificationScheduler() {
        this.schedulers.clear();
    }
}
