package ch.epfl.balelecbud.notifications.concertDataBase;

import java.util.List;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationSchedulerInterface;
import ch.epfl.balelecbud.schedule.models.Slot;

public interface ConcertDatabaseInterface {
    void addNotificationScheduler(NotificationSchedulerInterface scheduler);

    List<Slot> getAllConcert();

    void addNewConcert(Slot newSlot);
}
