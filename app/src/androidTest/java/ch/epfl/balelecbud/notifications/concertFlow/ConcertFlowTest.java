package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;
import android.content.Intent;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
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

import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.slot1;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.slot2;

@RunWith(AndroidJUnit4.class)
public class ConcertFlowTest {
    private ConcertOfInterestDatabase db;
    private ConcertFlow flow;

    @Before
    public void setup() {
        this.flow = new ConcertFlow();
        this.db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ConcertOfInterestDatabase.class
        ).build();
        this.flow.setDb(db);
    }

    @After
    public void tearDown() {
        this.db.close();
    }

    @Test
    public void notificationSchedulerIsCalledWhenNewConcertOfInterestAdded() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        this.flow.setNotificationScheduler(new NotificationSchedulerInterface() {
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
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        sync.waitCall(1);
        sync.assertCalled(1);
    }

    @Test
    public void notificationSchedulerIsCalledWhenMultipleNewConcertOfInterestAdded() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        flow.setNotificationScheduler(getScheduler(sync, s -> sync.fail()));
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        sync.waitCall(1);
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        sync.waitCall(2);
        sync.assertCalled(2);
    }

    @Test
    public void notificationsUnscheduledWhenConcertRemoved() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        flow.setNotificationScheduler(getScheduler(sync, slot -> {
            sync.assertEquals(slot1, slot);
            sync.call();
        }));
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        sync.waitCall(1);
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        sync.waitCall(2);
        flow.onHandleIntent(FlowUtil.packCancelIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        sync.waitCall(3);
        sync.assertCalled(3);
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
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        launchAndCheck(2, true, true);
    }

    @Test
    public void removedConcertAreNotInTheList() throws InterruptedException {
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        flow.onHandleIntent(FlowUtil.packCancelIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        launchAndCheck( 1, true, false);
    }

    private void launchAndCheck(int size, boolean containsSlot1, boolean containsSlot2) throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        flow.setLauncher(intent -> {
            List<Slot> slots = FlowUtil.unpackCallback(intent);
            sync.assertNotNull(slots);
            sync.assertEquals(size, slots.size());
            sync.assertEquals(containsSlot1, slots.contains(slot1));
            sync.assertEquals(containsSlot2, slots.contains(slot2));
            sync.call();
        });
        Intent intent = new Intent();
        intent.setAction(FlowUtil.GET_ALL_CONCERT);
        intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent());
        flow.onHandleIntent(intent);
        sync.waitCall(1);
        sync.assertCalled(1);
    }
}
