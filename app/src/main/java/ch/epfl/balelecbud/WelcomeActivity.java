package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.annotation.NonNull;

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.location.LocationUtil.Action;

public class WelcomeActivity extends BasicActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Switch locationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        this.configureToolBar(R.id.root_activity_toolbar);
        this.configureDrawerLayout(R.id.root_activity_drawer_layout);
        this.configureNavigationView(R.id.root_activity_nav_view);
        setUpLocation();
    }

    private void setUpLocation() {
        this.locationSwitch = findViewById(R.id.locationSwitch);
        this.locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "Location switched: ON");
                LocationUtil.enableLocation();
            } else {
                Log.d(TAG, "Location switched: OFF");
                LocationUtil.disableLocation();
            }
        });
        LocationUtil.requestLocationPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE) {
            LocationUtil.onLocationRequestPermissionsResult(
                    grantResults,
                    onPermissionNotGranted("Permission request canceled"),
                    onPermissionGranted(),
                    onPermissionNotGranted("Permission denied")
            );
        }
    }

    private Action onPermissionNotGranted(final String logText) {
        return () -> {
            Log.i(TAG, "onRequestPermissionsResult: " + logText);
            WelcomeActivity.this.locationSwitch.setClickable(false);
            WelcomeActivity.this.runOnUiThread(() ->
                    WelcomeActivity.this.locationSwitch.setChecked(false));
        };
    }

    private Action onPermissionGranted() {
        return () -> {
            Log.i(TAG, "onRequestPermissionsResult: Permission granted");
            WelcomeActivity.this.locationSwitch.setClickable(true);
            if (LocationUtil.isLocationActive()) {
                Log.d(TAG, "onPermissionGranted: location was active");
                WelcomeActivity.this.runOnUiThread(() ->
                        WelcomeActivity.this.locationSwitch.setChecked(true));
            }
        };
    }
}