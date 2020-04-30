package ch.epfl.balelecbud.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.location.LocationUtil;

public class SettingsMainFragment extends Fragment {

    public static final String TAG = SettingsMainFragment.class.getSimpleName();
    private SettingsFragment fragment;

    public static SettingsMainFragment newInstance() {
        return (new SettingsMainFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        fragment = new SettingsFragment();
        getActivity().getSupportFragmentManager()
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
