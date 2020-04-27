package ch.epfl.balelecbud.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Map;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.cloudMessaging.Message;

public class NotificationMessage implements NotificationInterface<Map<String, String>> {

    private static final String CHANNEL_ID = "MESSAGE_CHANNEL_ID";

    private static NotificationMessage singleInstance;

    private NotificationMessage() {

    }

    public static NotificationMessage getInstance() {
        if(singleInstance == null) {
            singleInstance = new NotificationMessage();
        }
        return singleInstance;
    }

    @Override
    public void scheduleNotification(Context context, Map<String, String> object) {
        int notificationId = object.hashCode();

        String type = object.get(Message.DATA_KEY_TYPE);

        Intent intent = getIntent(context, type);

        PendingIntent activity = PendingIntent.getActivity(context, notificationId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        android.app.Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(object.get(Message.DATA_KEY_TITLE))
                .setContentText(object.get(Message.DATA_KEY_BODY))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.my_notification_icone)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(activity)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);
    }

    private Intent getIntent(Context context, String type) {
        return new Intent(context, WelcomeActivity.class);
    }

    @Override
    public void cancelNotification(Context context, Map<String, String> object) {

    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.message_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}