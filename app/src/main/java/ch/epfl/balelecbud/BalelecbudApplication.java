package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import androidx.annotation.VisibleForTesting;

import ch.epfl.balelecbud.utility.authentication.Authenticator;
import ch.epfl.balelecbud.utility.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.utility.cache.Cache;
import ch.epfl.balelecbud.utility.cache.FilesystemCache;
import ch.epfl.balelecbud.utility.cloudMessaging.CloudMessagingService;
import ch.epfl.balelecbud.utility.cloudMessaging.MessagingService;
import ch.epfl.balelecbud.utility.connectivity.AndroidConnectivityChecker;
import ch.epfl.balelecbud.utility.connectivity.ConnectivityChecker;
import ch.epfl.balelecbud.utility.database.CachedDatabase;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.FirestoreDatabase;
import ch.epfl.balelecbud.utility.http.HttpClient;
import ch.epfl.balelecbud.utility.http.VolleyHttpClient;
import ch.epfl.balelecbud.utility.notifications.NotificationMessage;
import ch.epfl.balelecbud.utility.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.utility.storage.CachedStorage;
import ch.epfl.balelecbud.utility.storage.FirebaseStorage;
import ch.epfl.balelecbud.utility.storage.Storage;

/**
 * Central Balelecbud application
 */
public final class BalelecbudApplication extends Application {

    private static Context appContext;
    private static Storage appStorage;
    private static Storage remoteStorage;
    private static Database appDatabase;
    private static Cache appCache;
    private static Database remoteDatabase;
    private static Authenticator appAuthenticator;
    private static HttpClient httpClient;
    private static MessagingService appMessagingService;
    private static ConnectivityChecker connectivityChecker;

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

    public static Storage getAppStorage() {
        return appStorage;
    }

    public static ConnectivityChecker getConnectivityChecker() { return connectivityChecker; }

    public static Storage getRemoteStorage() {
        return remoteStorage;
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
        if (remoteStorage == null)
            remoteStorage = FirebaseStorage.getInstance();
        if (appStorage == null)
            appStorage = new CachedStorage(new ch.epfl.balelecbud.utility.storage.FilesystemCache());
        if (httpClient == null)
            httpClient = VolleyHttpClient.getInstance();
        if (appMessagingService == null)
            appMessagingService = CloudMessagingService.getInstance();
        if (connectivityChecker == null)
            connectivityChecker = AndroidConnectivityChecker.getInstance();
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

    @VisibleForTesting
    public static void setAppStorage(Storage storage) {
        appStorage = storage;
    }

    @VisibleForTesting
    public static void setConnectivityChecker(ConnectivityChecker connectivityChecker) {
        BalelecbudApplication.connectivityChecker = connectivityChecker;
    }

    @VisibleForTesting
    public static void setAppCache(Cache cache) {
        appCache = cache;
    }

    @VisibleForTesting
    public static void setRemoteStorage(Storage remoteStorage) {
        BalelecbudApplication.remoteStorage = remoteStorage;
    }
}
