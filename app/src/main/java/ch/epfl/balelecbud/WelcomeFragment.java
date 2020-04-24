package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import ch.epfl.balelecbud.location.LocationUtil;

public class WelcomeFragment extends Fragment {
    private static final String TAG = RootActivity.class.getSimpleName();
    private Switch locationSwitch;
    private FragmentActivity activity;

    public static WelcomeFragment newInstance() {
        return (new WelcomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = this.getActivity();
        setUpLocation();
    }

    private void setUpLocation() {
        this.locationSwitch = getView().findViewById(R.id.locationSwitch); // Not sure about this
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

    private Runnable onPermissionNotGranted(final String logText) {
        return () -> {
            Log.i(TAG, "onRequestPermissionsResult: " + logText);
            locationSwitch.setClickable(false);
            activity.runOnUiThread(() ->
                    locationSwitch.setChecked(false));
        };
    }

    private Runnable onPermissionGranted() {
        return () -> {
            Log.i(TAG, "onRequestPermissionsResult: Permission granted");
            locationSwitch.setClickable(true);
            if (LocationUtil.isLocationActive()) {
                Log.d(TAG, "onPermissionGranted: location was active");
                activity.runOnUiThread(() ->
                        locationSwitch.setChecked(true));
            }
        };
    }
}