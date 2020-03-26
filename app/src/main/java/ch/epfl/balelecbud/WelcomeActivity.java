package ch.epfl.balelecbud;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;

import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.location.FusedLocationClientAdapter;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationService;

public class WelcomeActivity extends BasicActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Switch locationSwitch;
    private boolean isLocalizationActive;

    public static final int LOCATION_PERMISSIONS_REQUEST_CODE = 34;
    private static final long UPDATE_INTERVAL = 60_000;
    private static final long FASTEST_UPDATE_INTERVAL = 30_000;
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL;

    private LocationRequest locationRequest;
    private LocationClient client;

    @VisibleForTesting
    public static boolean mockMode = false;

    @VisibleForTesting
    public void setLocationClient(LocationClient client) {
        this.client = client;
    }

    public boolean isLocationSwitchClickable() {
        return this.locationSwitch.isClickable();
    }

    public boolean isLocationActive() {
        return isLocalizationActive;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        bindActivityToButton(FestivalInformationActivity.class, (Button) findViewById(R.id.infoButton));
        bindActivityToButton(ScheduleActivity.class, (Button) findViewById(R.id.scheduleButton));
        bindActivityToButton(MapViewActivity.class, (Button) findViewById(R.id.mapButton));
        bindActivityToButton(TransportActivity.class, (Button) findViewById(R.id.transportButton));
        bindActivityToButton(PointOfInterestActivity.class, (Button) findViewById(R.id.poiButton));
        bindActivityToButton(SocialActivity.class, (Button) findViewById(R.id.socialButton));

        final Button signOutButton = findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        setUpLocation();
    }

    private void bindActivityToButton(final Class activityToOpen, Button button) {
        final Activity thisActivity = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, activityToOpen);
                startActivity(intent);
            }
        });
    }

    private void setUpLocation() {
        this.isLocalizationActive = false;

        this.locationSwitch = findViewById(R.id.locationSwitch);
        this.locationSwitch.setClickable(false);

        requestPermissions();
        if (checkPermissions()) {
            this.locationSwitch.setClickable(true);
        }

        this.locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The location is enabled
                    Log.d(TAG, "Location switched: ON");
                    requestLocationUpdates();
                } else {
                    // The location is disabled
                    Log.d(TAG, "Location switched: OFF");
                    removeLocationUpdates();
                }
            }
        });

        if (!mockMode) {
            client = new FusedLocationClientAdapter(this);
        }
        createLocationRequest();
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Check the permission required for the location service
     *
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
        locationRequest = new LocationRequest();

        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(MAX_WAIT_TIME);
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
                    LOCATION_PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                onPermissionCanceled();
            } else if (isLocationPermissionGranted(grantResults)) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }
        }
    }

    private boolean isLocationPermissionGranted(@NonNull int[] grantResults) {
        return (grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED);
    }

    private void onPermissionCanceled() {
        Log.i(TAG, "onRequestPermissionsResult: Permission request canceled");
        this.locationSwitch.setClickable(false);
    }

    private void onPermissionDenied() {
        Log.i(TAG, "onRequestPermissionsResult: Permission denied");
        this.locationSwitch.setClickable(false);
    }

    private void onPermissionGranted() {
        Log.i(TAG, "onRequestPermissionsResult: Permission granted");
        this.locationSwitch.setClickable(true);
    }

    /**
     * Handles the Request Updates button and requests start of location updates.
     */
    private void requestLocationUpdates() {
        try {
            Log.i(TAG, "Starting location updates");
            client.requestLocationUpdates(locationRequest, getPendingIntent());
            this.isLocalizationActive = true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */
    private void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        client.removeLocationUpdates(getPendingIntent());
        this.isLocalizationActive = false;
    }

    private void signOut() {
        getAuthenticator().signOut();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }
}
