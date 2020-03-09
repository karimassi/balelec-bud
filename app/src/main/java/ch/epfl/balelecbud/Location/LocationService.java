package ch.epfl.balelecbud.Location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;

public class LocationService extends IntentService {
    public static final String ACTION_PROCESS_UPDATES =
            "ch.epfl.balelecbud.location.LocationService.PROCESS_UPDATES";

    private static final String TAG = LocationService.class.getSimpleName();

    LocationFirestore lf;

    public LocationService() {
        super(TAG);
        lf = new FireBaseLocationAdapter();
    }

    public LocationService(LocationFirestore lf) {
        super(TAG);
        this.lf = lf;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    lf.handleLocation(location);
                }
            }
        }
    }
}
