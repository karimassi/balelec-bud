package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class MapViewActivity extends BasicActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    private double defaultLat;
    private double defaultLng;

    private LatLng position;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        defaultLat = Double.parseDouble(getString(R.string.default_lat));
        defaultLng = Double.parseDouble(getString(R.string.default_lng));
        LatLng default_location = new LatLng(defaultLat, defaultLng);

        setPosition(default_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(position).title("defaultPosition"));
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    public void setPosition(LatLng position) {
        if (position != null) {
            this.position = position;
        }
    }

    public LatLng getPosition() {
        return this.position;
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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