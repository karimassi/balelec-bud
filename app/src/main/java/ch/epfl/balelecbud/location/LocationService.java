package ch.epfl.balelecbud.location;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.android.gms.location.LocationResult;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class LocationService extends IntentService {
    private static final String TAG = LocationService.class.getSimpleName();
    public static final String ACTION_PROCESS_UPDATES = TAG + ".ACTION_PROCESS_UPDATES";

    private static Authenticator authenticator = FirebaseAuthenticator.getInstance();
    private static DatabaseWrapper databaseWrapper = FirestoreDatabaseWrapper.getInstance();

    private User user;

    @VisibleForTesting
    public static void setAuthenticator(Authenticator auth) {
        authenticator = auth;
    }

    @VisibleForTesting
    public static void setDatabase(DatabaseWrapper db) {
        databaseWrapper = db;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user = getAuthenticator().getCurrentUser();
        if (user == null) {
            LocationUtil.disableLocation();
        }
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public DatabaseWrapper getDatabase() {
        return databaseWrapper;
    }

    public LocationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && ACTION_PROCESS_UPDATES.equals(intent.getAction())) {
            handleLocationFromIntent(intent);
        }
    }

    private void handleLocationFromIntent(@NonNull Intent intent) {
        LocationResult result = LocationResult.extractResult(intent);
        if (result != null && result.getLastLocation() != null) {
            Log.d(TAG, "handleLocationFromIntent: userId = " + this.user.getUid());
            getDatabase().storeDocumentWithID(
                    DatabaseWrapper.LOCATIONS_PATH,
                    this.user.getUid(),
                    new Location(result.getLastLocation())
            );
        }
    }
}
