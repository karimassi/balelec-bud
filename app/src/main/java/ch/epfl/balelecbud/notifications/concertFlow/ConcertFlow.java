package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Room;

import java.util.Arrays;

import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDAO;
import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDatabase;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ConcertFlow extends AbstractConcertFlow {
    public interface IntentLauncher {
        void launchIntent(@NonNull Intent intent);
    }

    private static final String TAG = ConcertFlow.class.getSimpleName();
    private NotificationSchedulerInterface scheduler;
    private ConcertOfInterestDAO concertOfInterestDAO;
    private ConcertOfInterestDatabase db;

    public ConcertFlow() {
        super(TAG);
    }

    private IntentLauncher launcher = new IntentLauncher() {
        @Override
        public void launchIntent(@NonNull Intent intent) {
            ConcertFlow.this.startActivity(intent);
        }
    };

    @VisibleForTesting
    public void setLauncher(IntentLauncher launcher) {
        this.launcher = launcher;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        db = Room.databaseBuilder(this,
                ConcertOfInterestDatabase.class, "ConcertsOfInterest").build();
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
        if (db != null)
            db.close();
        this.db = concertDb;
        concertOfInterestDAO = concertDb.getConcertOfInterestDAO();
    }

    @VisibleForTesting
    public void setNotificationScheduler(NotificationSchedulerInterface scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    protected void getAllScheduledConcert(Intent callbackIntent) {
        Slot[] res = this.concertOfInterestDAO.getAllConcertOfInterest();
        Log.d(TAG, "getAllScheduledConcert: " + Arrays.toString(res));
        this.launcher.launchIntent(FlowUtil.packCallback(res, callbackIntent));
    }

    @Override
    protected void scheduleNewConcert(final Slot newSlot) {
        if (!concertOfInterestDAO.getAllConcertOfInterestList().contains(newSlot)) {
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
