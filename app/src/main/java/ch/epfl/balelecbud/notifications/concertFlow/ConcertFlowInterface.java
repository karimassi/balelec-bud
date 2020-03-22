package ch.epfl.balelecbud.notifications.concertFlow;

import androidx.arch.core.util.Function;

import java.util.List;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

public interface ConcertFlowInterface {
    void addNotificationScheduler(NotificationSchedulerInterface scheduler);

    void getAllScheduledConcert(Function<List<Slot>, Void> callback);

    void scheduleNewConcert(Slot newSlot);

    void removeConcert(Slot slot);

    void close();
}
