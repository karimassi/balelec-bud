package ch.epfl.balelecbud;

<<<<<<< HEAD
import android.Manifest;
import android.app.PendingIntent;
=======
>>>>>>> master
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
<<<<<<< HEAD
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.location.FusedLocationClientAdapter;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationService;
=======

import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.location.LocationUtil.Action;
import ch.epfl.balelecbud.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.util.intents.FlowUtil;
>>>>>>> master


public class WelcomeActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Switch locationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

<<<<<<< HEAD
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        /**final Button signOutButton = findViewById(R.id.sign_out_button);
=======
        bindActivityToButton(FestivalInformationActivity.class, (Button) findViewById(R.id.infoButton));
        bindActivityToButton(ScheduleActivity.class, (Button) findViewById(R.id.scheduleButton));
        bindActivityToButton(MapViewActivity.class, (Button) findViewById(R.id.mapButton));
        bindActivityToButton(TransportActivity.class, (Button) findViewById(R.id.transportButton));
        bindActivityToButton(PointOfInterestActivity.class, (Button) findViewById(R.id.poiButton));
        bindActivityToButton(SocialActivity.class, (Button) findViewById(R.id.socialButton));
        bindScheduleActivityToButton();
        final Button signOutButton = findViewById(R.id.buttonSignOut);
>>>>>>> master
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
<<<<<<< HEAD
        });**/
        setUpLocation();
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
            case R.id.sign_out_button:
                signOut();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.root_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.root_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.root_activity_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
=======
        });


        setUpLocation();
    }

    private void bindScheduleActivityToButton() {
        Button button = findViewById(R.id.scheduleButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, ConcertFlow.class);
                intent.setAction(FlowUtil.GET_ALL_CONCERT);
                intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent(WelcomeActivity.this, ScheduleActivity.class));
                startService(intent);
            }
        });
    }

    private void bindActivityToButton(final Class activityToOpen, Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, activityToOpen);
                startActivity(intent);
            }
        });
>>>>>>> master
    }

    private void setUpLocation() {
        this.locationSwitch = findViewById(R.id.locationSwitch);
        this.locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "Location switched: ON");
                    LocationUtil.enableLocation(WelcomeActivity.this);
                } else {
                    Log.d(TAG, "Location switched: OFF");
                    LocationUtil.disableLocation(WelcomeActivity.this);
                }
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
        return new Action() {
            @Override
            public void perform() {
                Log.i(TAG, "onRequestPermissionsResult: " + logText);
                WelcomeActivity.this.locationSwitch.setClickable(false);
                WelcomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WelcomeActivity.this.locationSwitch.setChecked(false);
                    }
                });
            }
        };
    }

    private Action onPermissionGranted() {
        return new Action() {
            @Override
            public void perform() {
                Log.i(TAG, "onRequestPermissionsResult: Permission granted");
                WelcomeActivity.this.locationSwitch.setClickable(true);
                if (LocationUtil.isLocationActive(WelcomeActivity.this)) {
                    Log.d(TAG, "onPermissionGranted: location was active");
                    WelcomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WelcomeActivity.this.locationSwitch.setChecked(true);
                        }
                    });
                }
            }
        };
    }

    private void signOut() {
        getAuthenticator().signOut();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }
}