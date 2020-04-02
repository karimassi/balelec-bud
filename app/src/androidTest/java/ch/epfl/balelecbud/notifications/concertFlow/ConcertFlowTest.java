package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;
import android.content.Intent;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.Timestamp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDatabase;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.intents.FlowUtil;

import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ConcertFlowTest {
    static private Slot slot1;
    static private Slot slot2;

    private ConcertOfInterestDatabase db;
    private ConcertFlow flow;

    @BeforeClass
    public static void setUpSlots(){
        List<Timestamp> timestamps = new LinkedList<>();
        for(int i = 0; i < 4; ++i){
            Calendar c = Calendar.getInstance();
            c.set(2020,11,11,10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3)) ;

        Room.databaseBuilder(ApplicationProvider.getApplicationContext(),
                ConcertOfInterestDatabase.class, "ConcertsOfInterest")
                .build().clearAllTables();
    }

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
        final List<Object> sync = new ArrayList<>();
        this.flow.setNotificationScheduler(new NotificationSchedulerInterface() {
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                Assert.assertEquals(slot, slot1);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }

            @Override
            public void cancelNotification(Context context, Slot slot) {
                Assert.fail();
            }

        });
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertFalse(sync.isEmpty());
    }

    @Test
    public void notificationSchedulerIsCalledWhenMultipleNewConcertOfInterestAdded() throws InterruptedException {
        final List<Object> sync = new ArrayList<>();
        flow.setNotificationScheduler(new NotificationSchedulerInterface() {
            private int received = 0;
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                if (received == 0)
                    Assert.assertEquals(slot, slot1);
                else
                    Assert.assertEquals(slot, slot2);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
                ++received;
            }

            @Override
            public void cancelNotification(Context context, Slot slot) {
                Assert.fail();
            }

        });
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        synchronized (sync) {
            sync.wait(1000);
        }
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(2));
    }

    @Test
    public void notificationsUnscheduledWhenConcertRemoved() throws InterruptedException {
        final List<Object> sync = new ArrayList<>();
        flow.setNotificationScheduler(new NotificationSchedulerInterface() {
            private int received = 0;
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                if (received == 0)
                    Assert.assertEquals(slot, slot1);
                else
                    Assert.assertEquals(slot, slot2);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
                ++received;
            }

            @Override
            public void cancelNotification(Context context, Slot slot) {
                Assert.assertEquals(slot, slot1);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }

        });
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        synchronized (sync) {
            sync.wait(1000);
        }
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        synchronized (sync) {
            sync.wait(1000);
        }
        flow.onHandleIntent(FlowUtil.packCancelIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(3));
    }

    @Test
    public void addedConcertsAreInTheList() throws InterruptedException {
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        final List<Object> sync = new LinkedList<>();
        flow.setLauncher(intent -> {
            List<Slot> res = FlowUtil.unpackCallback(intent);
            Assert.assertNotNull(res);
            Assert.assertThat(res.size(), is(2));
            Assert.assertTrue(res.contains(slot1));
            Assert.assertTrue(res.contains(slot2));
            synchronized (sync) {
                sync.add(new Object());
                sync.notify();
            }
        });
        Intent intent = new Intent();
        intent.setAction(FlowUtil.GET_ALL_CONCERT);
        intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent());
        flow.onHandleIntent(intent);
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(1));
    }

    @Test
    public void removedConcertAreNotInTheList() throws InterruptedException {
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        flow.onHandleIntent(FlowUtil.packSubscribeIntentWithSlot(ApplicationProvider.getApplicationContext(), slot2));
        flow.onHandleIntent(FlowUtil.packCancelIntentWithSlot(ApplicationProvider.getApplicationContext(), slot1));
        final List<Object> sync = new LinkedList<>();
        flow.setLauncher(intent -> {
            List<Slot> slots = FlowUtil.unpackCallback(intent);
            Assert.assertNotNull(slots);
            Assert.assertEquals(slots.size(), 1);
            Assert.assertFalse(slots.contains(slot1));
            Assert.assertTrue(slots.contains(slot2));
            synchronized (sync) {
                sync.add(new Object());
                sync.notify();
            }
        });
        Intent intent = new Intent();
        intent.setAction(FlowUtil.GET_ALL_CONCERT);
        intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent());
        flow.onHandleIntent(intent);
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(1));
    }

}
