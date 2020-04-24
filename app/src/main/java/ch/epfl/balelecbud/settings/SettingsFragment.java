package ch.epfl.balelecbud.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.location.LocationUtil;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        findPreference(LocationUtil.LOCATION_ENABLE_KEY).setOnPreferenceChangeListener((preference, new_value) -> {
            LocationUtil.updateLocation((boolean) new_value);
            return true;
        });
        findPreference("locationInfo").setOnPreferenceClickListener(preference -> {
            LocationUtil.requestLocationPermission(getActivity());
            return true;
        });
        boolean permissionGranted =
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        updatePreferencesVisibility(permissionGranted);
    }

    void updatePreferencesVisibility(boolean permissionGranted){

        findPreference(LocationUtil.LOCATION_ENABLE_KEY).setVisible(permissionGranted);
        findPreference("locationInfo").setVisible(!permissionGranted);
    }
}