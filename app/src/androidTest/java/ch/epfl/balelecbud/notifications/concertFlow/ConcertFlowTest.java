package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;
import android.content.Intent;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.function.Consumer;

import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDatabase;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.intents.FlowUtil;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot2;

@RunWith(AndroidJUnit4.class)
public class ConcertFlowTest {
    private ConcertOfInterestDatabase db;

    @Before
    public void setup() {
        this.db = Room.inMemoryDatabaseBuilder(
                getApplicationContext(),
                ConcertOfInterestDatabase.class
        ).build();
        ConcertFlow.setMockDb(db);
        setDummyNotificationScheduler();
    }

    private void setDummyNotificationScheduler() {
        ConcertFlow.setNotificationScheduler(new NotificationSchedulerInterface() {
            @Override
            public void scheduleNotification(Context context, Slot slot) { }

            @Override
            public void cancelNotification(Context context, Slot slot) { }
        });
    }

    @After
    public void tearDown() {
        Assert.assertTrue(this.db.isOpen());
        this.db.clearAllTables();
        this.db.close();
    }

    @Test
    public void notificationSchedulerIsCalledWhenNewConcertOfInterestAdded() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        ConcertFlow.setNotificationScheduler(new NotificationSchedulerInterface() {
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                sync.assertEquals(slot1, slot);
                sync.call();
            }

            @Override
            public void cancelNotification(Context context, Slot slot) {
                sync.fail();
            }
        });
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot1));
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void notificationSchedulerIsCalledWhenMultipleNewConcertOfInterestAdded() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        ConcertFlow.setNotificationScheduler(getScheduler(sync, s -> sync.fail()));
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot1));
        sync.waitCall(1);
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot2));
        sync.waitCall(2);
        sync.assertCalled(2);
        sync.assertNoFailedTests();
    }

    @Test
    public void notificationsUnscheduledWhenConcertRemoved() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        ConcertFlow.setNotificationScheduler(getScheduler(sync, slot -> {
            sync.assertEquals(slot1, slot);
            sync.call();
        }));
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot1));
        sync.waitCall(1);
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot2));
        sync.waitCall(2);
        getApplicationContext().startService(FlowUtil.packCancelIntentWithSlot(getApplicationContext(), slot1));
        sync.waitCall(3);
        sync.assertCalled(3);
        sync.assertNoFailedTests();
    }

    @NotNull
    private NotificationSchedulerInterface getScheduler(TestAsyncUtils sync,
                                                        Consumer<Slot> cancel) {
        return new NotificationSchedulerInterface() {
            private int received = 0;
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                if (received == 0)
                    sync.assertEquals(slot1, slot);
                else
                    sync.assertEquals(slot2, slot);
                ++received;
                sync.call();
            }

            @Override
            public void cancelNotification(Context context, Slot slot) {
                cancel.accept(slot);
            }
        };
    }

    @Test
    public void addedConcertsAreInTheList() throws InterruptedException {
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot1));
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot2));
        launchAndCheck(2, true, true);
    }

    @Test
    public void removedConcertAreNotInTheList() throws InterruptedException {
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot1));
        getApplicationContext().startService(FlowUtil.packSubscribeIntentWithSlot(getApplicationContext(), slot2));
        getApplicationContext().startService(FlowUtil.packCancelIntentWithSlot(getApplicationContext(), slot1));
        launchAndCheck( 1, false, true);
    }

    private void launchAndCheck(int size, boolean containsSlot1, boolean containsSlot2) throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        ConcertFlow.setLauncher(intent -> {
            List<Slot> slots = FlowUtil.unpackCallback(intent);
            sync.assertNotNull(slots);
            sync.assertEquals(size, slots.size());
            sync.assertEquals(containsSlot1, slots.contains(slot1));
            sync.assertEquals(containsSlot2, slots.contains(slot2));
            sync.call();
        });
        Intent intent = new Intent(getApplicationContext(), ConcertFlow.class);
        intent.setAction(FlowUtil.GET_ALL_CONCERT);
        intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent());
        getApplicationContext().startService(intent);
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }
}
