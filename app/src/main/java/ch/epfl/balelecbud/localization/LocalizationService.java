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
    private static Activity activity;
//    public static final int LOCATION_REQUEST_CODE = "ch.epfl.balelecbud.localization.localizationService.REQUEST".hashCode();
//    public static final int PERMISSION_REQUEST_CODE = 100;
//    private static LocalizationService localizationService = null;

//    private final LocationRequest lr;
//    private final LocationSettingsRequest lsr;
//    private final PendingIntent pi;
//    private final FusedLocationProviderClient client;
//    private Location lastLocation = null;

//    public static void getLocalizationService(Activity activity) {
//
//        Log.d(TAG, "Before Constructor");
//        LocalizationService.activity = activity;
//        if (localizationService == null)
//            localizationService = new LocalizationService(activity);
//        return localizationService;
//    }

    public LocalizationService() {
        super(TAG);
//        this.activity = activity;
//        this.lr = LocationRequest.create()
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            .setInterval(2 * 1000);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(lr)
//                .setAlwaysShow(false);
//
//        this.lsr = builder.build();
//
//
////        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
////        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Intent i = new Intent(activity, LocalizationService.class);
//        i.setAction(LocalizationService.ACTION_PROCESS_UPDATES);
//        this.pi = PendingIntent.getService(
//                activity,
//                LocalizationService.LOCATION_REQUEST_CODE,
//                i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        this.client = LocationServices.getFusedLocationProviderClient(activity);
    }

//    private void checkPermission() {
//        Task<LocationSettingsResponse> result =
//                LocationServices.getSettingsClient(this.activity).checkLocationSettings(this.lsr);
//
//        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
//            @Override
//            public void onComplete(Task<LocationSettingsResponse> task) {
//                try {
//                    LocationSettingsResponse response = task.getResult(ApiException.class);
//                    // All location settings are satisfied. The client can initialize location
//                    // requests here.
//                    LocalizationService.this.initializeLocalization();
//                } catch (ApiException exception) {
//                    switch (exception.getStatusCode()) {
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            Toast.makeText(activity, "Location permission needed", Toast.LENGTH_SHORT).show();
//                            // Location settings are not satisfied. But could be fixed by showing the
//                            // user a dialog.
//                            try {
//                                // Cast to a resolvable exception.
//                                ResolvableApiException resolvable = (ResolvableApiException) exception;
//                                // Show the dialog by calling startResolutionForResult(),
//                                // and check the result in onActivityResult().
//                                resolvable.startResolutionForResult(
//                                        activity,
//                                        LocalizationService.PERMISSION_REQUEST_CODE);
//                            } catch (IntentSender.SendIntentException e) {
//                                // Ignore the error.
//                            } catch (ClassCastException e) {
//                                // Ignore, should be an impossible error.
//                            }
//                            break;
//                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                            // Location settings are not satisfied. However, we have no way to fix the
//                            // settings so we won't show the dialog.
//                            Toast.makeText(LocalizationService.this, "Location unavailable", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                }
//            }
//        });
//    }

//    private void initializeLocalization() {
//        Log.d("fghj", "coucou ici");
//        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if (task.isSuccessful()) {
//                    displayLocation(task.getResult());
//                } else {
//                    Log.d("gvhbn", "lastLocation failed");
//                }
//            }
//        });
//        //client.requestLocationUpdates(lr, pi);
//    }

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
            //Toast.makeText(activity, String.format(Locale.US, "lat: %1$f, lon: %2$f", latitude, longitude), Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Location not available");
            //Toast.makeText(this.getApplicationContext(), "Location not availible", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        //checkPermission();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        //client.removeLocationUpdates(this.pi);
//    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "coucou from IntentHandle");
        if (intent != null) {
            Log.d(TAG, "intent non null");
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                Log.d(TAG, "intent is for me ");
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Log.d(TAG, "Intent has a Location");
                    Location locations = result.getLastLocation();
                    handleLocation(locations);
//                    Utils.setLocationUpdatesResult(this, locations);
//                    Utils.sendNotification(this, Utils.getLocationResultTitle(this, locations));
//                    Log.i(TAG, Utils.getLocationUpdatesResult(this));
                } else {
                    Log.d(TAG, "Intent has no Location :-(");
                }
            }
        }
    }
}
