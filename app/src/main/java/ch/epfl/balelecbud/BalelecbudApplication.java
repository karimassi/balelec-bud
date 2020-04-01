package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;

public class BalelecbudApplication extends Application {
    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        NotificationScheduler.getInstance().createNotificationChannel(appContext);
    }
}
