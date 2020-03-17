package ch.epfl.balelecbud.notifications.concertSoon;

import ch.epfl.balelecbud.schedule.models.Slot;

public interface SchedulerInterface {

    void scheduleNotification(Slot slot);

}
