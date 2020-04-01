package ch.epfl.balelecbud.notifications.concertSoon;

import android.content.Context;

import ch.epfl.balelecbud.schedule.models.Slot;

public interface NotificationSchedulerInterface {

    void scheduleNotification(Context context, Slot slot);

    void cancelNotification(Context context, Slot slot);

}
