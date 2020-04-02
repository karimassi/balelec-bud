package ch.epfl.balelecbud;

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

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {
    private final float DEFAULT_ZOOM = 17;

    private Location location;
    private GoogleMap googleMap;
    private Task<android.location.Location> locationResult;
    private static boolean locationEnabled;

    private final OnCompleteListener<android.location.Location> callback =
            new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {
                    setLocationFrom(task.getResult(), task.isSuccessful());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLng(location), DEFAULT_ZOOM));
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setDefaultLocation();
        setLocationPermission();
        setLocationResult();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.setMyLocationEnabled(locationEnabled);
        googleMap.getUiSettings().setMyLocationButtonEnabled(locationEnabled);

        if(locationEnabled)
            locationResult.addOnCompleteListener(this, callback);
        else {
            googleMap.addMarker(new MarkerOptions().position(getLatLng(location)).title("Default Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLng(location), DEFAULT_ZOOM));
        }
    }

    protected void setLocationResult() {
        if(locationEnabled) {
            locationResult = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
        }
        else {
            locationResult = null;
        }
    }

    protected void setLocationPermission() {
        locationEnabled = LocationUtil.isLocationActive();
    }

    protected void setLocationFrom(android.location.Location deviceLocation, boolean locationEnabled) {
        if(locationEnabled && deviceLocation != null) {
            location = new Location(deviceLocation);
        }
    }

    protected void setLocationFrom(LatLng latLng) {
        if(latLng != null) {
            location = new Location(latLng);
        }
    }

    protected void setLocation(Location location) {
        if (location != null) {
            this.location = location;
        }
    }

    private void setDefaultLocation() {
        final double defaultLat = Double.parseDouble(getString(R.string.default_lat));
        final double defaultLng = Double.parseDouble(getString(R.string.default_lng));
        final Location defaultLocation = new Location(defaultLat, defaultLng);
        setLocation(defaultLocation);
    }

    public Location getLocation() {
        return location;
    }

    public static boolean getLocationPermission() {
        return locationEnabled;
    }

    public Task<android.location.Location> getLocationResult() {
        return locationResult;
    }

    public LatLng getLatLng(Location location) {
        if(location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        else return null;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }
}