package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.friendship.SocialActivity;
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
        this.locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "Location switched: ON");
                    LocationUtil.enableLocation(WelcomeActivity.this);
                } else {
                    Log.d(TAG, "Location switched: OFF");
                    LocationUtil.disableLocation(WelcomeActivity.this);
                }
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
        return new Action() {
            @Override
            public void perform() {
                Log.i(TAG, "onRequestPermissionsResult: " + logText);
                WelcomeActivity.this.locationSwitch.setClickable(false);
                WelcomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WelcomeActivity.this.locationSwitch.setChecked(false);
                    }
                });
            }
        };
    }

    private Action onPermissionGranted() {
        return new Action() {
            @Override
            public void perform() {
                Log.i(TAG, "onRequestPermissionsResult: Permission granted");
                WelcomeActivity.this.locationSwitch.setClickable(true);
                if (LocationUtil.isLocationActive(WelcomeActivity.this)) {
                    Log.d(TAG, "onPermissionGranted: location was active");
                    WelcomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WelcomeActivity.this.locationSwitch.setChecked(true);
                        }
                    });
                }
            }
        };
    }

}