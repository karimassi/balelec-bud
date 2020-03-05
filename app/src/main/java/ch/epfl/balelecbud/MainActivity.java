package ch.epfl.balelecbud;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import ch.epfl.balelecbud.localization.LocalizationService;

/**
 * @note Localization service inspired from https://github.com/android/location-samples
 */
public class MainActivity extends AppCompatActivity { //implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String EXTRA_MESSAGE = "ch.epfl.balelecbud.MESSAGE";
    private Switch ls;
    private boolean isLocalizationActive;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final long UPDATE_INTERVAL = 6_000;
    private static final long FASTEST_UPDATE_INTERVAL = 3_000;
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL; // Every times a value is ready

    private LocationRequest lr;
    private FusedLocationProviderClient client;

    public boolean isLocalizationActive() {
        return isLocalizationActive;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.isLocalizationActive = false;

        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            this.ls.setClickable(true);
        }

        this.ls = findViewById(R.id.localizationSwitch);

        this.ls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The localization is enabled
                    Log.d(TAG, "Localization switched: ON");
                    requestLocationUpdates();
                } else {
                    // The localization is disabled
                    Log.d(TAG,"Localization switched: OFF");
                    removeLocationUpdates();
                }
            }
        });


        client = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
    }

    private void createLocationRequest() {
        lr = new LocationRequest();

        lr.setInterval(UPDATE_INTERVAL);
        lr.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lr.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocalizationService.class);
        intent.setAction(LocalizationService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Check the permission required for the localization service
     * @return true if the application has the required permission
     */
    private boolean checkPermissions() {
        int fineLocationPermissionState = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);

        int backgroundLocationPermissionState = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        return (fineLocationPermissionState == PackageManager.PERMISSION_GRANTED) &&
                (backgroundLocationPermissionState == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request the localization permission
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION },
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
                this.ls.setClickable(false);
            } else if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED)
            ) {
                // Permission was granted.
                Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                this.ls.setClickable(true);
            }
        }
    }

    /**
     * Handles the Request Updates button and requests start of location updates.
     */
    public void requestLocationUpdates() {
        try {
            Log.i(TAG, "Starting location updates");
            client.requestLocationUpdates(lr, getPendingIntent());
            this.isLocalizationActive = true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        client.removeLocationUpdates(getPendingIntent());
        this.isLocalizationActive = false;
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, GreetingActivity.class);
        EditText editText = findViewById(R.id.mainTextEdit);
        String message = "Hello "  + editText.getText() + "!";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
