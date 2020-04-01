package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;

public class MapViewActivity extends BasicActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    private final float DEFAULT_ZOOM = 17;

    private Location location;
    private GoogleMap googleMap;
    private Task<android.location.Location> locationResult;
    private static boolean locationEnabled;

    private OnCompleteListener<android.location.Location> callback =
            new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {
                    setLocationFrom(task.getResult(), task.isSuccessful());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLng(location), DEFAULT_ZOOM));
                }
            };

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.setMyLocationEnabled(locationEnabled);
        googleMap.getUiSettings().setMyLocationButtonEnabled(locationEnabled);

        if(locationEnabled) locationResult.addOnCompleteListener(this, callback);
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
        locationEnabled = LocationUtil.isLocationActive(this);
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

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.activity_main_drawer_info :
                Intent intentInfo = new Intent(this, FestivalInformationActivity.class);
                startActivity(intentInfo);
                break;
            case R.id.activity_main_drawer_schedule:
                Intent intentSchedule = new Intent(this, ScheduleActivity.class);
                startActivity(intentSchedule);
                break;
            case R.id.activity_main_drawer_poi:
                Intent intentPoi = new Intent(this, PointOfInterestActivity.class);
                startActivity(intentPoi);
                break;
            case R.id.activity_main_drawer_map:
                Intent intentMap = new Intent(this, MapViewActivity.class);
                startActivity(intentMap);
                break;
            case R.id.activity_main_drawer_transport:
                Intent intentTransport = new Intent(this, TransportActivity.class);
                startActivity(intentTransport);
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureToolBar(){
        this.toolbar = findViewById(R.id.map_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.map_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.map_activity_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}