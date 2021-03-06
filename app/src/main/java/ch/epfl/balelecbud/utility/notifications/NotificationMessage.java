package ch.epfl.balelecbud.utility.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Map;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.view.friendship.SocialFragment;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;

/**
 * Notification scheduler for Firebase Cloud Messaging
 */
public final class NotificationMessage implements NotificationInterface<Map<String, String>> {

    private static final String CHANNEL_ID = "MESSAGE_CHANNEL_ID";

    private static NotificationMessage singleInstance;

    private NotificationMessage() { }

    public static NotificationMessage getInstance() {
        if(singleInstance == null) {
            singleInstance = new NotificationMessage();
        }
        return singleInstance;
    }

    @Override
    public void scheduleNotification(Context context, Map<String, String> object) {
        int notificationId = object.hashCode();

        String type = object.get(context.getString(R.string.data_key_type));

        Intent intent = getIntent(context, type);

        PendingIntent activity = PendingIntent.getActivity(context, notificationId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        android.app.Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(object.get(getAppContext().getString(R.string.data_key_title)))
                .setContentText(object.get(getAppContext().getString(R.string.data_key_body)))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_friend_request)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(activity)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);
    }

    private Intent getIntent(Context context, String type) {
        Log.d("NotificationMessage", "type: " + type);
        if(type.equals(context.getString(R.string.message_type_social))) {
            return new Intent(context, SocialFragment.class);
        }
        return new Intent(context, RootActivity.class);
    }

    @Override
    public void cancelNotification(Context context, Map<String, String> object) {}

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.message_channel_name), NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}