package ch.epfl.balelecbud.view.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceFragmentCompat;

import java.util.function.Supplier;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.location.LocationUtils;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

public final class SettingsFragment extends PreferenceFragmentCompat {
    enum ConnectionStatus {
        SIGNED_OUT(),
        SIGNED_IN,
        CONNECTING;

        boolean isSignedIn() {
            return this == SIGNED_IN;
        }

        boolean isSignedOut() {
            return this == SIGNED_OUT;
        }

        boolean isConnecting() {
            return this == CONNECTING;
        }
    }
    public static String TAG = SettingsFragment.class.getSimpleName();
    private String LOCATION_ENABLE_KEY;
    private String LOCATION_INFO_KEY;
    private String SIGN_IN_KEY;
    private String SIGN_OUT_KEY;
    private String DELETE_USER_KEY;
    private String CONNECTING_KEY;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.black));
        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        LOCATION_ENABLE_KEY = getString(R.string.location_enable_key);
        LOCATION_INFO_KEY = getString(R.string.location_info_key);
        SIGN_IN_KEY = getString(R.string.sign_in_key);
        SIGN_OUT_KEY = getString(R.string.sign_out_key);
        DELETE_USER_KEY = getString(R.string.delete_user_key);
        CONNECTING_KEY = getString(R.string.connection_key);

        setUpLocationPreferences();
        setUpLoginPreferences();
    }

    private void setUpLoginPreferences() {
        linkPreferenceWithDialog(SIGN_IN_KEY, () -> LoginUserFragment.newInstance(this), LoginUserFragment.TAG);
        findPreference(SIGN_OUT_KEY).setOnPreferenceClickListener(preference -> {
            getAppAuthenticator().signOut();
            getAppAuthenticator().signInAnonymously();
            if (LocationUtils.isLocationActive()) {
                LocationUtils.disableLocation();
                updateLocationPreferencesVisibility(false);
            }
            updateLoginStatus(ConnectionStatus.SIGNED_OUT);
            return true;
        });
        linkPreferenceWithDialog(DELETE_USER_KEY, () -> DeleteAccountDialog.newInstance(this), DeleteAccountDialog.TAG);

        updateLoginStatus(getAppAuthenticator().getCurrentUser() != null ? ConnectionStatus.SIGNED_IN : ConnectionStatus.SIGNED_OUT);
    }

    private void linkPreferenceWithDialog(String preferenceKey, Supplier<DialogFragment> dialogSupplier, String tag) {
        findPreference(preferenceKey).setOnPreferenceClickListener(preference -> {
            DialogFragment dialog = dialogSupplier.get();
            dialog.show(getParentFragmentManager(), tag);
            return true;
        });
    }

    private void setUpLocationPreferences() {
        findPreference(LOCATION_ENABLE_KEY).setOnPreferenceChangeListener((preference, new_value) -> {
            if ((boolean) new_value) {
                LocationUtils.enableLocation();
            } else {
                LocationUtils.disableLocation();
            }
            return true;
        });
        findPreference(LOCATION_INFO_KEY).setOnPreferenceClickListener(preference -> {
            LocationUtils.requestLocationPermission(this);
            return true;
        });
        boolean permissionGranted =
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        updateLocationPreferencesVisibility(permissionGranted);
    }

    private void updateLocationPreferencesVisibility(boolean permissionGranted) {
        findPreference(LOCATION_ENABLE_KEY).setVisible(permissionGranted);
        findPreference(LOCATION_INFO_KEY).setVisible(!permissionGranted);
    }

    void updateLoginStatus(ConnectionStatus status) {
        findPreference(SIGN_IN_KEY).setVisible(status.isSignedOut());
        findPreference(CONNECTING_KEY).setVisible(status.isConnecting());
        findPreference(SIGN_OUT_KEY).setVisible(status.isSignedIn());
        findPreference(DELETE_USER_KEY).setVisible(status.isSignedIn());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LocationUtils.LOCATION_PERMISSIONS_REQUEST_CODE) {
            LocationUtils.onLocationRequestPermissionsResult(
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