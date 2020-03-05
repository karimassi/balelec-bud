package ch.epfl.balelecbud.localization;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;

import java.util.Locale;

public class LocalizationService extends IntentService {
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    final static String CHANNEL_ID = "channel_01";
    public static final String ACTION_PROCESS_UPDATES =
            "ch.epfl.balelecbud.localization.LocalizationService.PROCESS_UPDATES";

    private static final String TAG = LocalizationService.class.getSimpleName();

    public LocalizationService() {
        super(TAG);
    }

    private void handleLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            final String str = String.format(Locale.US, "lat: %1$f, lon: %2$f", latitude, longitude);
            Log.d(TAG, str);
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location locations = result.getLastLocation();
                    handleLocation(locations);
                }
            }
        }
    }
}
