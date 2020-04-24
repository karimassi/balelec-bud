package ch.epfl.balelecbud.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.BasicActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.location.LocationUtil;

public class SettingsActivity extends BasicActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private SettingsFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        this.configureToolBar(R.id.settings_activity_toolbar);
        this.configureDrawerLayout(R.id.settings_activity_drawer_layout);
        this.configureNavigationView(R.id.settings_activity_nav_view);

        fragment = new SettingsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE) {
            LocationUtil.onLocationRequestPermissionsResult(
                    grantResults,
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission request canceled");
                        fragment.updatePreferencesVisibility(false);
                    },
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission granted");
                        fragment.updatePreferencesVisibility(true);
                    },
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission denied");
                        fragment.updatePreferencesVisibility(false);
                    });
        }
    }
}
