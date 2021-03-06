package ch.epfl.balelecbud.utility.notifications.concertSoon;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ch.epfl.balelecbud.utility.FlowUtils;

/**
 * Broadcast receiver to handle communication with the system notification service
 */
public final class NotificationProvider extends BroadcastReceiver {

    public final static String NOTIFICATION_ID = "notification_id";
    public final static String NOTIFICATION = "notification";
    public final static String SLOT_ID = "slot_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(notificationId, notification);

        Intent ackIntent = FlowUtils.packAckIntentWithId(context, intent.getIntExtra(SLOT_ID, -1));
        context.startService(ackIntent);
    }
}
