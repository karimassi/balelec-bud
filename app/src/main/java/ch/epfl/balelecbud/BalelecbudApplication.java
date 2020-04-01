package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;

public class BalelecbudApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context ctx = getApplicationContext();
        NotificationScheduler.getInstance().createNotificationChannel(ctx);
    }
}
