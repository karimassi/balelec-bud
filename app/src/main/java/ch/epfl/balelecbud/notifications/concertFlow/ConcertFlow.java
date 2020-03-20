package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
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

    @Override
    public void addNotificationScheduler(NotificationSchedulerInterface scheduler) {
        schedulers.add(scheduler);
    }

    @Override
    public List<Slot> getAllScheduledConcert() {
        return concertOfInterestDAO.getAllConcertOfInterest();
    }

    @Override
    public void scheduleNewConcert(Slot newSlot) {
        concertOfInterestDAO.insertConcert(newSlot);
        for (NotificationSchedulerInterface notificationScheduler : this.schedulers)
            notificationScheduler.scheduleNotification(this.context, newSlot);
    }

    @Override
    public void removeConcert(Slot slot) {
        concertOfInterestDAO.removeConcert(slot);
        for (NotificationSchedulerInterface notificationScheduler : this.schedulers)
            notificationScheduler.cancelNotification(this.context, slot);
    }

    @Override
    public void close() {
        this.db.close();
    }

    @VisibleForTesting
    public void clearNotificationScheduler() {
        this.schedulers.clear();
    }
}
