package ch.epfl.balelecbud.notifications.concertFlow;

import java.util.List;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ConcertFlow implements ConcertFlowInterface {
    @Override
    public void addNotificationScheduler(NotificationSchedulerInterface scheduler) {
        //stub implementation
    }

    @Override
    public List<Slot> getAllScheduledConcert() {
        //stub implementation
        return null;
    }

    @Override
    public void scheduleNewConcert(Slot newSlot) {
        //stub implementation
    }

    @Override
    public void removeConcert(Slot slot) {

    }
}
