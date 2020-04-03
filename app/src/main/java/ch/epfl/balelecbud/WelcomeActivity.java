package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;

import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.location.LocationUtil.Action;
import ch.epfl.balelecbud.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.util.intents.FlowUtil;

public class WelcomeActivity extends BasicActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Switch locationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        bindActivityToButton(FestivalInformationActivity.class, findViewById(R.id.infoButton));
        bindActivityToButton(ScheduleActivity.class, findViewById(R.id.scheduleButton));
        bindActivityToButton(MapViewActivity.class, findViewById(R.id.mapButton));
        bindActivityToButton(TransportActivity.class, findViewById(R.id.transportButton));
        bindActivityToButton(PointOfInterestActivity.class, findViewById(R.id.poiButton));
        bindActivityToButton(SocialActivity.class, findViewById(R.id.socialButton));
        bindScheduleActivityToButton();
        final Button signOutButton = findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(v -> signOut());


        setUpLocation();
    }

    private void bindScheduleActivityToButton() {
        Button button = findViewById(R.id.scheduleButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, ConcertFlow.class);
            intent.setAction(FlowUtil.GET_ALL_CONCERT);
            intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent(WelcomeActivity.this, ScheduleActivity.class));
            startService(intent);
        });
    }

    private void bindActivityToButton(final Class activityToOpen, Button button) {
        button.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, activityToOpen);
            startActivity(intent);
        });
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

    private void signOut() {
        getAuthenticator().signOut();
        if (LocationUtil.isLocationActive())
            LocationUtil.disableLocation();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }
}
