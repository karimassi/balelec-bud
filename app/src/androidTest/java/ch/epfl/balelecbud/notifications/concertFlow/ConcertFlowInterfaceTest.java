package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;

import androidx.arch.core.util.Function;
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

import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ConcertFlowInterfaceTest {
    static private Slot slot1;
    static private Slot slot2;
    static private Slot slot3;

    private ConcertFlowInterface flow;
    private ConcertOfInterestDatabase db;

    @BeforeClass
    public static void setUpSlots(){
        List<Timestamp> timestamps = new LinkedList<>();
        for(int i = 0; i < 6; ++i){
            Calendar c = Calendar.getInstance();
            c.set(2020,11,11,10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3)) ;
        slot3 = new Slot(2, "Upset", "Scène Sat'",  timestamps.get(4), timestamps.get(5));

        Room.databaseBuilder(ApplicationProvider.getApplicationContext(),
                ConcertOfInterestDatabase.class, "ConcertsOfInterest")
                .build().clearAllTables();
    }

    @Before
    public void setup() {
        this.flow = new ConcertFlow(ApplicationProvider.getApplicationContext());
        this.db =  Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ConcertOfInterestDatabase.class
        ).build();
        ((ConcertFlow) this.flow).setDb(db);
    }

    @After
    public void tearDown() {
        ((ConcertFlow) flow).clearNotificationScheduler();
        flow.removeConcert(slot1);
        flow.removeConcert(slot2);
        flow.removeConcert(slot3);
        this.db.clearAllTables();
        this.flow.close();
    }

    @Test
    public void notificationSchedulerIsCalledWhenNewConcertOfInterestAdded() throws InterruptedException {
        final Slot s = slot1;
        final List<Object> sync = new ArrayList<>();
        flow.addNotificationScheduler(new NotificationSchedulerInterface() {
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                Assert.assertEquals(slot, s);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }

            @Override
            public void cancelNotification(Context context, Slot slot) {
                Assert.fail();
            }

            @Override
            public void onNotificationPushed(int id) {
                Assert.fail();
            }
        });
        flow.scheduleNewConcert(s);
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertFalse(sync.isEmpty());
    }

    @Test
    public void notificationSchedulerIsCalledWhenMultipleNewConcertOfInterestAdded() throws InterruptedException {
        final Slot s1 = slot1;
        final Slot s2 = slot2;
        final List<Object> sync = new ArrayList<>();
        flow.addNotificationScheduler(new NotificationSchedulerInterface() {
            private int received = 0;
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                if (received == 0)
                    Assert.assertEquals(slot, s1);
                else
                    Assert.assertEquals(slot, s2);
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

            @Override
            public void onNotificationPushed(int id) {
                Assert.fail();
            }
        });
        flow.scheduleNewConcert(s1);
        synchronized (sync) {
            sync.wait(1000);
        }
        flow.scheduleNewConcert(s2);
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(2));
    }

    @Test
    public void notificationsUnscheduledWhenConcertRemoved() throws InterruptedException {
        final Slot s1 = slot1;
        final Slot s2 = slot2;
        final List<Object> sync = new ArrayList<>();
        flow.addNotificationScheduler(new NotificationSchedulerInterface() {
            private int received = 0;
            @Override
            public void scheduleNotification(Context context, Slot slot) {
                if (received == 0)
                    Assert.assertEquals(slot, s1);
                else
                    Assert.assertEquals(slot, s2);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
                ++received;
            }

            @Override
            public void cancelNotification(Context context, Slot slot) {
                Assert.assertEquals(slot, s1);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }

            @Override
            public void onNotificationPushed(int id) {
                Assert.fail();
            }
        });
        flow.scheduleNewConcert(s1);
        synchronized (sync) {
            sync.wait(1000);
        }
        flow.scheduleNewConcert(s2);
        synchronized (sync) {
            sync.wait(1000);
        }
        flow.removeConcert(s1);
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(3));
    }

    @Test
    public void addedConcertAreInTheList() throws InterruptedException {
        flow.scheduleNewConcert(slot1);
        flow.scheduleNewConcert(slot2);
        final List<Object> sync = new LinkedList<>();
        flow.getAllScheduledConcert(new Function<List<Slot>, Void>() {
            @Override
            public Void apply(List<Slot> input) {
                Assert.assertEquals(input.size(), 2);
                Assert.assertTrue(input.contains(slot1));
                Assert.assertTrue(input.contains(slot2));
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
                return null;
            }
        });
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(1));
    }

    @Test
    public void removedConcertAreNotInTheList() throws InterruptedException {
        flow.scheduleNewConcert(slot1);
        flow.scheduleNewConcert(slot2);
        flow.removeConcert(slot1);
        final List<Object> sync = new LinkedList<>();
        flow.getAllScheduledConcert(new Function<List<Slot>, Void>() {
            @Override
            public Void apply(List<Slot> input) {
                Assert.assertEquals(input.size(), 1);
                Assert.assertFalse(input.contains(slot1));
                Assert.assertTrue(input.contains(slot2));
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
                return null;
            }
        });
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(1));
    }

}
