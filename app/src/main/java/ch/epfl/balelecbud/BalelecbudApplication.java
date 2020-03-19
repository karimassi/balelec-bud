package ch.epfl.balelecbud;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationProvider;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;

public class BalelecbudApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context ctx = getApplicationContext();
        NotificationScheduler.getInstance().createNotificationChannel(ctx);
    }
}
