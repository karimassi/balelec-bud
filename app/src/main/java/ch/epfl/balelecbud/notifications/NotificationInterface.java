package ch.epfl.balelecbud.notifications;

import android.content.Context;

public interface NotificationInterface<T> {

    void scheduleNotification(Context context, T object);

    void cancelNotification(Context context, T object);
}
