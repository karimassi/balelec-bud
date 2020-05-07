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
import ch.epfl.balelecbud.util.cache.Cache;
import ch.epfl.balelecbud.util.cache.FilesystemCache;
import ch.epfl.balelecbud.util.database.CachedDatabase;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.FirestoreDatabase;
import ch.epfl.balelecbud.util.http.HttpClient;
import ch.epfl.balelecbud.util.http.VolleyHttpClient;

public class BalelecbudApplication extends Application {

    private static Context appContext;
    private static Database appDatabase;
    private static Cache appCache;
    private static Database remoteDatabase;
    private static Authenticator appAuthenticator;
    private static HttpClient httpClient;
    private static MessagingService appMessagingService;

    public static Context getAppContext() {
        return appContext;
    }

    public static Database getAppDatabase() {
        return appDatabase;
    }

    public static Authenticator getAppAuthenticator() {
        return appAuthenticator;
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }

    public static Database getRemoteDatabase() {
        return remoteDatabase;
    }

    public static Cache getAppCache() {
        return appCache;
    }

    public static MessagingService getMessagingService() {
        return appMessagingService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        if (remoteDatabase == null)
            remoteDatabase = FirestoreDatabase.getInstance();
        if (appCache == null)
            appCache = FilesystemCache.getInstance();
        if (appDatabase == null)
            appDatabase = CachedDatabase.getInstance();
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
    public static void setAppDatabase(Database database) {
        appDatabase = database;
    }

    @VisibleForTesting
    public static void setRemoteDatabase(Database database) {
        remoteDatabase = database;
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
