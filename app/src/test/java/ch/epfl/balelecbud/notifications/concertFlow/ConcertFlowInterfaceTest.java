package ch.epfl.balelecbud.notifications.concertFlow;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

import static org.hamcrest.core.Is.is;

@RunWith(JUnit4.class)
public class ConcertFlowInterfaceTest {
    @Test
    public void notificationSchedulerIsCalledWhenNewConcertOfInterestAdded() throws InterruptedException {
        final Slot s = new Slot("abc", "def", "11-11");
        final List<Object> sync = new ArrayList<>();
        ConcertFlowInterface flow = new ConcertFlow();
        flow.addNotificationScheduler(new NotificationSchedulerInterface() {
            @Override
            public void scheduleNotification(Slot slot) {
                Assert.assertThat(slot, is(s));
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
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
        final Slot s1 = new Slot("abc", "def", "11-11");
        final Slot s2 = new Slot("abc", "def", "11-11");
        final List<Object> sync = new ArrayList<>();
        ConcertFlowInterface flow = new ConcertFlow();
        flow.addNotificationScheduler(new NotificationSchedulerInterface() {
            private int received = 0;
            @Override
            public void scheduleNotification(Slot slot) {
                if (received == 0)
                    Assert.assertThat(slot, is(s1));
                else
                    Assert.assertThat(slot, is(s2));
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
                ++received;
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
    public void addedConcertAreInTheList() {
        final Slot s1 = new Slot("abc", "def", "11-11");
        final Slot s2 = new Slot("abc", "def", "11-11");

        ConcertFlowInterface flow = new ConcertFlow();
        flow.scheduleNewConcert(s1);
        flow.scheduleNewConcert(s2);
        Assert.assertTrue(flow.getAllScheduledConcert().contains(s1));
        Assert.assertTrue(flow.getAllScheduledConcert().contains(s2));
    }

    @Test
    public void removedConcertAreNotInTheList() {
        final Slot s1 = new Slot("abc", "def", "11-11");
        final Slot s2 = new Slot("abc", "def", "11-11");

        ConcertFlowInterface flow = new ConcertFlow();
        flow.scheduleNewConcert(s1);
        flow.scheduleNewConcert(s2);
        flow.removeConcert(s1);
        Assert.assertFalse(flow.getAllScheduledConcert().contains(s1));
        Assert.assertTrue(flow.getAllScheduledConcert().contains(s2));
    }

}
