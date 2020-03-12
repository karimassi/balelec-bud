package ch.epfl.balelecbud.Location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

public class LocationService extends IntentService {
    public static final String ACTION_PROCESS_UPDATES =
            "ch.epfl.balelecbud.location.LocationService.PROCESS_UPDATES";

    private static final String TAG = LocationService.class.getSimpleName();

    private final LocationFirestore locationFirestore;
    private final OnCompleteListener<Void> callback;

    public LocationService() {
        super(TAG);
        this.locationFirestore = new FireBaseLocationAdapter();
        this.callback = new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Location successfully sent");
                } else {
                    Log.w(TAG, "onComplete: Failed to send the Location", task.getException());
                }
            }
        };
    }

    public LocationService(LocationFirestore locationFirestore, OnCompleteListener<Void> callback) {
        super(TAG);
        this.locationFirestore = locationFirestore;
        this.callback = callback;
    }

    private GeoPoint transformToGeoPoint(Location l) {
        if (l == null)
            return new GeoPoint(0,0);
        return new GeoPoint(l.getLatitude(), l.getLongitude());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                handleLocationFromIntent(intent);
            }
        }
    }

    private void handleLocationFromIntent(@NonNull Intent intent) {
        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {
            locationFirestore.handleGeoPoint(transformToGeoPoint(result.getLastLocation()), this.callback);
        }
    }
}
