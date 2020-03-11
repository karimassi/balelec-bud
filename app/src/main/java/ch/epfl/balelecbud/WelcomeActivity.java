package ch.epfl.balelecbud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;

import ch.epfl.balelecbud.Authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.Location.LocationService;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Switch ls;
    private boolean isLocalizationActive;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final long UPDATE_INTERVAL = 60_000;
    private static final long FASTEST_UPDATE_INTERVAL = 30_000;
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL;

    private LocationRequest lr;
    private FusedLocationProviderClient client;

    public boolean isLocationSwitchClickable() {
        return this.ls.isClickable();
    }

    public boolean isLocationActive() {
        return isLocalizationActive;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        this.isLocalizationActive = false;

        this.ls = findViewById(R.id.locationSwitch);
        this.ls.setClickable(false);
        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            this.ls.setClickable(true);
        }


        this.ls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The location is enabled
                    Log.d(TAG, "Location switched: ON");
                    requestLocationUpdates();
                } else {
                    // The location is disabled
                    Log.d(TAG,"Location switched: OFF");
                    removeLocationUpdates();
                }
            }
        });


        client = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

    }


    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Check the permission required for the location service
     * @return true if the application has the required permission
     */
    private boolean checkPermissions() {
        int fineLocationPermissionState = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);

        int backgroundLocationPermissionState =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ?
                        PackageManager.PERMISSION_GRANTED :
                        ActivityCompat.checkSelfPermission(
                                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        return (fineLocationPermissionState == PackageManager.PERMISSION_GRANTED) &&
                (backgroundLocationPermissionState == PackageManager.PERMISSION_GRANTED);
    }

    private void createLocationRequest() {
        lr = new LocationRequest();

        lr.setInterval(UPDATE_INTERVAL);
        lr.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lr.setMaxWaitTime(MAX_WAIT_TIME);
    }

    /**
     * Request the location permission
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult (PERMISSION_GRANTED = " + PackageManager.PERMISSION_GRANTED + ")");
        Log.d(TAG, "onRequestPermissionsResult: Permission = " + Arrays.toString(permissions));
        Log.d(TAG, "onRequestPermissionsResult: grantResults = " + Arrays.toString(grantResults));
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
                this.ls.setClickable(false);
            } else if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)
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

    private void signOut() {
        FirebaseAuthenticator.getInstance().signOut();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.buttonSignOut) {
            signOut();
            Intent intent = new Intent(this, LoginUserActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
