package ch.epfl.balelecbud.location;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.Database;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public class LocationService extends IntentService {
    private static final String TAG = LocationService.class.getSimpleName();
    public static final String ACTION_PROCESS_UPDATES = TAG + ".ACTION_PROCESS_UPDATES";

    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        user = getAppAuthenticator().getCurrentUser();
        if (user == null) {
            LocationUtil.disableLocation();
        }
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
            android.location.Location lastLocation = result.getLastLocation();
            getAppDatabase().storeDocumentWithID(
                    Database.LOCATIONS_PATH,
                    this.user.getUid(),
                    new Location(lastLocation.getLatitude(), lastLocation.getLongitude())
            );
        }
    }
}
