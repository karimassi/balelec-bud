package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import androidx.annotation.VisibleForTesting;

import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.util.http.HttpClient;
import ch.epfl.balelecbud.util.http.VolleyHttpClient;

public class BalelecbudApplication extends Application {

    private static Context appContext;
    private static HttpClient httpClient;

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
        if (httpClient == null) {
            httpClient = VolleyHttpClient.getInstance();
        }
        NotificationScheduler.getInstance().createNotificationChannel(appContext);
    }

    @VisibleForTesting
    public static void setHttpClient(HttpClient client) {
        httpClient = client;
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }

}
