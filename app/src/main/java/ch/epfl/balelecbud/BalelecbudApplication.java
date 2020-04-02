package ch.epfl.balelecbud;

import android.app.Application;
import android.content.Context;

import androidx.annotation.VisibleForTesting;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class BalelecbudApplication extends Application {
    private static Context appContext;
    private static DatabaseWrapper appDatabaseWrapper;
    private static Authenticator appAuthenticator;

    public static Context getAppContext() {
        return appContext;
    }

    public static DatabaseWrapper getAppDatabaseWrapper() {
        return appDatabaseWrapper;
    }

    public static Authenticator getAppAuthenticator() {
        return appAuthenticator;
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

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        if (appDatabaseWrapper == null)
            appDatabaseWrapper = FirestoreDatabaseWrapper.getInstance();
        if (appAuthenticator == null)
            appAuthenticator = FirebaseAuthenticator.getInstance();
        NotificationScheduler.getInstance().createNotificationChannel(appContext);
    }
}
