package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import androidx.annotation.VisibleForTesting;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;

public class BalelecbudApplication extends Application {
    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @VisibleForTesting
    public static void setAppContext(Context context) {
        appContext = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        NotificationScheduler.getInstance().createNotificationChannel(appContext);
    }
}
