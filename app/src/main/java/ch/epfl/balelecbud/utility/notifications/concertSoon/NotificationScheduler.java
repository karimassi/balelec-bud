package ch.epfl.balelecbud.utility.notifications.concertSoon;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.utility.notifications.NotificationInterface;
import ch.epfl.balelecbud.model.Slot;

public class NotificationScheduler implements NotificationInterface<Slot> {

    private static final String TAG = "ConcertSoon.Scheduler";
    private static final String CHANNEL_ID = "CONCERT_SOON_CHANNEL_ID";
    private static final String CONCERT_SOON_ACTION = "ch.epfl.balelecbud.util.notifications.concertSoon.ACTION";

    private static NotificationScheduler single_instance;

    private final Map<Integer, PendingIntent> pendingIntents = new HashMap<>();

    //private constructor to ensure singleton
    private NotificationScheduler() { }

    public static NotificationScheduler getInstance() {
        if (single_instance == null) {
            single_instance = new NotificationScheduler();
        }
        return single_instance;
    }


    @Override
    public void scheduleNotification(Context context, Slot object) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.concert_soon_notification_title))
                .setContentText(object.getArtistName() + " starts in 15 minutes on " + object.getSceneName())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.my_notification_icone)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //what should be launched when notification clicked
        Intent intent = new Intent(context, RootActivity.class);
        int notificationId = object.getId();
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, NotificationProvider.class);
        notificationIntent.putExtra(NotificationProvider.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationProvider.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationProvider.SLOT_ID, object.getId());
        notificationIntent.setAction(CONCERT_SOON_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntents.put(notificationId, pendingIntent);

        //calculate the waking up time
        Calendar cal = Calendar.getInstance();
        cal.setTime(object.getStartTime().toDate());
        cal.add(Calendar.MINUTE, -15);
        long timeToWakeUp = cal.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeToWakeUp, pendingIntent);
    }

    @Override
    public void cancelNotification(Context context, Slot object) {
        int id = object.getId();
        if (pendingIntents.containsKey(id)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntents.get(id));
            pendingIntents.remove(id);
        }
    }

    public void createNotificationChannel(Context ctx) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.concert_soon_channel_name);
            String description = ctx.getString(R.string.concert_soon_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
