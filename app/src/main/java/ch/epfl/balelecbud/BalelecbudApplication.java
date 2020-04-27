package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import androidx.annotation.VisibleForTesting;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.cloudMessaging.CloudMessagingService;
import ch.epfl.balelecbud.cloudMessaging.MessagingService;
import ch.epfl.balelecbud.notifications.NotificationMessage;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.http.HttpClient;
import ch.epfl.balelecbud.util.http.VolleyHttpClient;

public class BalelecbudApplication extends Application {

    private static Context appContext;
    private static DatabaseWrapper appDatabaseWrapper;
    private static Authenticator appAuthenticator;
    private static HttpClient httpClient;
    private static MessagingService appMessagingService;

    public static Context getAppContext() {
        return appContext;
    }

    public static DatabaseWrapper getAppDatabaseWrapper() {
        return appDatabaseWrapper;
    }

    public static Authenticator getAppAuthenticator() {
        return appAuthenticator;
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }

    public static MessagingService getMessagingService() {
        return appMessagingService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        if (appDatabaseWrapper == null)
            appDatabaseWrapper = FirestoreDatabaseWrapper.getInstance();
        if (appAuthenticator == null)
            appAuthenticator = FirebaseAuthenticator.getInstance();
        if (httpClient == null)
            httpClient = VolleyHttpClient.getInstance();
        if (appMessagingService == null)
            appMessagingService = CloudMessagingService.getInstance();
        NotificationScheduler.getInstance().createNotificationChannel(appContext);
        NotificationMessage.getInstance().createNotificationChannel(appContext);
    }

    @VisibleForTesting
    public static void setAppContext(Context context) {
        appContext = context;
    }

    @VisibleForTesting
    public static void setAppDatabaseWrapper(DatabaseWrapper databaseWrapper) {
        appDatabaseWrapper = databaseWrapper;
    }

    @VisibleForTesting
    public static void setAppAuthenticator(Authenticator authenticator) {
        appAuthenticator = authenticator;
    }

    @VisibleForTesting
    public static void setHttpClient(HttpClient client) {
        httpClient = client;
    }

    @VisibleForTesting
    public static void setAppMessagingService(MessagingService messagingService) {
        appMessagingService = messagingService;
    }
}
