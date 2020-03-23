package ch.epfl.balelecbud.notifications.concertFlow;

import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.room.Room;

import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDAO;
import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDatabase;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ConcertFlow extends AbstractConcertFlow {
    private static final String TAG = ConcertFlow.class.getSimpleName();
    private NotificationSchedulerInterface scheduler;
    private ConcertOfInterestDAO concertOfInterestDAO;
    private ConcertOfInterestDatabase db;

    public ConcertFlow() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        db = Room.databaseBuilder(this,
                ConcertOfInterestDatabase.class, "ConcertsOfInterest")
                .build();
        concertOfInterestDAO = db.getConcertOfInterestDAO();
        scheduler = NotificationScheduler.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        db.close();
    }

    @VisibleForTesting
    public void setDb(ConcertOfInterestDatabase concertDb) {
        db.close();
        concertOfInterestDAO = concertDb.getConcertOfInterestDAO();
    }

    @VisibleForTesting
    public void setNotificationScheduler(NotificationSchedulerInterface scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    protected void getAllScheduledConcert(AbstractConcertFlow.FlowCallback callback) {
        callback.onResult(concertOfInterestDAO.getAllConcertOfInterest());
    }

    @Override
    protected void scheduleNewConcert(final Slot newSlot) {
        if (!concertOfInterestDAO.getAllConcertOfInterest().contains(newSlot)) {
            concertOfInterestDAO.insertConcert(newSlot);
        }
        if (scheduler != null)
            scheduler.scheduleNotification(ConcertFlow.this, newSlot);
    }

    @Override
    protected void removeConcert(final Slot slot) {
        concertOfInterestDAO.removeConcert(slot);
        if (scheduler != null)
            scheduler.cancelNotification(this, slot);
    }

    @Override
    protected void removeConcertById(int id) {
        this.concertOfInterestDAO.removeConcertById(id);
    }
}
