package ch.epfl.balelecbud.notifications.concertFlow;

import java.util.List;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

public interface ConcertFlowInterface {
    void addNotificationScheduler(NotificationSchedulerInterface scheduler);

    List<Slot> getAllScheduledConcert();

    void scheduleNewConcert(Slot newSlot);

    void removeConcert(Slot slot);

    void close();
}
