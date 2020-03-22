package ch.epfl.balelecbud;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private final float DEFAULT_ZOOM = 17;

    private LatLng position;
    private GoogleMap googleMap;
    private boolean locationEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final double defaultLat = Double.parseDouble(getString(R.string.default_lat));
        final double defaultLng = Double.parseDouble(getString(R.string.default_lng));
        final LatLng defaultLocation = new LatLng(defaultLat, defaultLng);

        setPosition(defaultLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(position).title("Default Position"));

        setLocationPermission();
        updateLocationUi();
        getDeviceLocation();
    }

    private void setLocationPermission() {
        locationEnabled = WelcomeActivity.isLocationActive();
    }

    private void updateLocationUi() {
        googleMap.setMyLocationEnabled(locationEnabled);
        googleMap.getUiSettings().setMyLocationButtonEnabled(locationEnabled);
    }

    private void getDeviceLocation() {
        if (locationEnabled) {
            Task<Location> locationResult = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    moveCameraTo(task.getResult(), task.isSuccessful());
                }
            });
        }
        else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
        }
    }

    protected void moveCameraTo(Location location, boolean locationEnabled) {
        if(locationEnabled) {
            setPositionFrom(location);
        }
        else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
    }

    public void setPosition(LatLng position) {
        if (position != null) {
            this.position = position;
        }
    }

    public void setPositionFrom(Location location) {
        if (location != null) {
            position = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    public LatLng getPosition() {
        return position;
    }
}