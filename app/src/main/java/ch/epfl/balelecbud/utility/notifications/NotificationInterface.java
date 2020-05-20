package ch.epfl.balelecbud.utility.notifications;

import android.content.Context;

/**
 * Interface for notification scheduling
 */
public interface NotificationInterface<T> {

    void scheduleNotification(Context context, T object);

    void cancelNotification(Context context, T object);
}
