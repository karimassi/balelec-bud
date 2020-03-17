package ch.epfl.balelecbud.notifications.concertSoon;

import ch.epfl.balelecbud.schedule.models.Slot;

public interface NotificationSchedulerInterface {

    void scheduleNotification(Slot slot);

}
