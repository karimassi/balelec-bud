package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import androidx.annotation.VisibleForTesting;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.FirestoreDatabase;
import ch.epfl.balelecbud.util.http.HttpClient;
import ch.epfl.balelecbud.util.http.VolleyHttpClient;

public class BalelecbudApplication extends Application {

    private static Context appContext;
    private static Database appDatabase;
    private static Authenticator appAuthenticator;
    private static HttpClient httpClient;

    public static Context getAppContext() {
        return appContext;
    }

    public static Database getAppDatabase() {
        return appDatabase;
    }

    public static Authenticator getAppAuthenticator() {
        return appAuthenticator;
    }

    @VisibleForTesting
    public static void setAppContext(Context context) {
        appContext = context;
    }

    @VisibleForTesting
    public static void setAppDatabase(Database database) {
        appDatabase = database;
    }

    @VisibleForTesting
    public static void setAppAuthenticator(Authenticator authenticator) {
        appAuthenticator = authenticator;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        if (appDatabase == null)
            appDatabase = FirestoreDatabase.getInstance();
        if (appAuthenticator == null)
            appAuthenticator = FirebaseAuthenticator.getInstance();
        if (httpClient == null)
            httpClient = VolleyHttpClient.getInstance();
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
