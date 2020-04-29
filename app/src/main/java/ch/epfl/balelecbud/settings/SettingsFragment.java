package ch.epfl.balelecbud.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.location.LocationUtil;

public class SettingsFragment extends PreferenceFragmentCompat {

    private String ENABLE_KEY;
    private String INFO_KEY;
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
            LocationUtil.requestLocationPermission(getParentFragment());
            return true;
        });
        boolean permissionGranted =
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        updatePreferencesVisibility(permissionGranted);
    }

    void updatePreferencesVisibility(boolean permissionGranted){

        findPreference(ENABLE_KEY).setVisible(permissionGranted);
        findPreference(INFO_KEY).setVisible(!permissionGranted);
    }
}