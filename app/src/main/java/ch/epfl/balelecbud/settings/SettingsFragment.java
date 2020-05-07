package ch.epfl.balelecbud.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.location.LocationUtil;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static String TAG = SettingsFragment.class.getSimpleName();
    private String LOCATION_ENABLE_KEY;
    private String LOCATION_INFO_KEY;
    private String SIGN_IN_KEY;
    private String SIGN_OUT_KEY;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        LOCATION_ENABLE_KEY = getString(R.string.location_enable_key);
        LOCATION_INFO_KEY = getString(R.string.location_info_key);
        SIGN_IN_KEY = getString(R.string.sign_in_key);
        SIGN_OUT_KEY = getString(R.string.sign_out_key);
        findPreference(LOCATION_ENABLE_KEY).setOnPreferenceChangeListener((preference, new_value) -> {
            LocationUtil.updateLocation((boolean) new_value);
            return true;
        });
        findPreference(LOCATION_INFO_KEY).setOnPreferenceClickListener(preference -> {
            LocationUtil.requestLocationPermission(this);
            return true;
        });
        findPreference(SIGN_IN_KEY).setOnPreferenceClickListener(preference -> {
            DialogFragment dialog = LoginUserFragment.newInstance(this);
            dialog.show(getParentFragmentManager(), LoginUserFragment.TAG);
            return true;
        });
        findPreference(SIGN_OUT_KEY).setOnPreferenceClickListener(preference -> {
            getAppAuthenticator().signOut();
            if (LocationUtil.isLocationActive()) {
                LocationUtil.disableLocation();
                updateLocationPreferencesVisibility(false);
            }
            updateLoginStatus(false);
            return true;
        });
        boolean permissionGranted =
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        updateLocationPreferencesVisibility(permissionGranted);
        updateLoginStatus(getAppAuthenticator().getCurrentUser() != null);
    }

    private void updateLocationPreferencesVisibility(boolean permissionGranted){
        findPreference(LOCATION_ENABLE_KEY).setVisible(permissionGranted);
        findPreference(LOCATION_INFO_KEY).setVisible(!permissionGranted);
    }

    void updateLoginStatus(boolean loggedIn) {
        findPreference(SIGN_IN_KEY).setVisible(!loggedIn);
        findPreference(SIGN_OUT_KEY).setVisible(loggedIn);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE) {
            LocationUtil.onLocationRequestPermissionsResult(
                    grantResults,
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission request canceled");
                        this.updateLocationPreferencesVisibility(false);
                    },
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission granted");
                        this.updateLocationPreferencesVisibility(true);
                    },
                    () -> {
                        Log.i(TAG, "onRequestPermissionsResult: Permission denied");
                        this.updateLocationPreferencesVisibility(false);
                    });
        }
    }
}