package ch.epfl.balelecbud.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.location.LocationUtil;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static String TAG = SettingsFragment.class.getSimpleName();
    private String ENABLE_KEY;
    private String INFO_KEY;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        ENABLE_KEY = getString(R.string.location_enable_key);
        INFO_KEY = getString(R.string.location_info_key);
        findPreference(ENABLE_KEY).setOnPreferenceChangeListener((preference, new_value) -> {
            LocationUtil.updateLocation((boolean) new_value);
            return true;
        });
        findPreference(INFO_KEY).setOnPreferenceClickListener(preference -> {
            LocationUtil.requestLocationPermission(this);
            return true;
        });
        boolean permissionGranted =
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        updatePreferencesVisibility(permissionGranted);
    }

    private void updatePreferencesVisibility(boolean permissionGranted) {

        findPreference(ENABLE_KEY).setVisible(permissionGranted);
        findPreference(INFO_KEY).setVisible(!permissionGranted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE) {
            LocationUtil.onLocationRequestPermissionsResult(
                    grantResults,
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission request canceled");
                        this.updatePreferencesVisibility(false);
                    },
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission granted");
                        this.updatePreferencesVisibility(true);
                    },
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission denied");
                        this.updatePreferencesVisibility(false);
                    });
        }
    }
}