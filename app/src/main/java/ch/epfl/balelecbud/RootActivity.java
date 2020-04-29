package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import ch.epfl.balelecbud.friendship.SocialFragment;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.map.MapViewFragment;
import ch.epfl.balelecbud.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestFragment;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.settings.SettingsMainFragment;
import ch.epfl.balelecbud.util.intents.FlowUtil;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;
import static ch.epfl.balelecbud.location.LocationUtil.disableLocation;
import static ch.epfl.balelecbud.location.LocationUtil.isLocationActive;

public class RootActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final String TAG = RootActivity.class.getSimpleName();

    private Fragment fragmentInfo;
    private ScheduleFragment fragmentSchedule;
    private Fragment fragmentPoi;
    private MapViewFragment fragmentMap;
    private Fragment fragmentTransport;
    private Fragment fragmentSocial;
    private WelcomeFragment fragmentHome;
    private Fragment fragmentEmergency;
    private Fragment fragmentEmergencyInfo;
    private Fragment fragmentEmergencyNumbers;
    private Fragment fragmentSettings;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_INFO = 1;
    private static final int FRAGMENT_SCHEDULE = 2;
    private static final int FRAGMENT_POI = 3;
    private static final int FRAGMENT_MAP = 4;
    private static final int FRAGMENT_TRANSPORT = 5;
    private static final int FRAGMENT_SOCIAL = 6;
    private static final int FRAGMENT_EMERGENCY = 7;
    private static final int FRAGMENT_EMERGENCY_INFO = 8;
    private static final int FRAGMENT_EMERGENCY_NUMBERS = 9;
    private static final int FRAGMENT_SETTINGS = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentSchedule = ScheduleFragment.newInstance();
        setContentView(R.layout.activity_root);
        this.configureToolBar(R.id.root_activity_toolbar);
        this.configureDrawerLayout(R.id.root_activity_drawer_layout);
        this.configureNavigationView(R.id.root_activity_nav_view);

        List<Slot> slots = FlowUtil.unpackCallback(getIntent());
        if (slots != null) {
            if (!fragmentSchedule.isVisible()) {
                fragmentSchedule.setSlots(slots);
                startTransactionFragment(fragmentSchedule, "SCHEDULE");
            }
        } else {
            this.showFirstFragment();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.activity_main_drawer_home:
                this.showFragment(FRAGMENT_HOME);
                break;
            case R.id.activity_main_drawer_info:
                this.showFragment(FRAGMENT_INFO);
                break;
            case R.id.activity_main_drawer_schedule:
                this.showFragment(FRAGMENT_SCHEDULE);
                break;
            case R.id.activity_main_drawer_poi:
                this.showFragment(FRAGMENT_POI);
                break;
            case R.id.activity_main_drawer_map:
                this.showFragment(FRAGMENT_MAP);
                break;
            case R.id.activity_main_drawer_transport:
                this.showFragment(FRAGMENT_TRANSPORT);
                break;
            case R.id.activity_main_drawer_social:
                this.showFragment(FRAGMENT_SOCIAL);
                break;
            case R.id.activity_main_drawer_emergency:
                this.showFragment(FRAGMENT_EMERGENCY);
                break;
            case R.id.activity_main_drawer_emergency_info:
                this.showFragment(FRAGMENT_EMERGENCY_INFO);
                break;
            case R.id.activity_main_drawer_emergency_numbers:
                this.showFragment(FRAGMENT_EMERGENCY_NUMBERS);
                break;
            case R.id.activity_main_drawer_settings:
                this.showFragment(FRAGMENT_SETTINGS);
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

    private void showFragment(int fragmentIdentifier) {
        switch (fragmentIdentifier) {
            case FRAGMENT_HOME:
                this.showHomeFragment();
                break;
            case FRAGMENT_INFO:
                this.showInfoFragment();
                break;
            case FRAGMENT_SCHEDULE:
                this.showScheduleFragment();
                break;
            case FRAGMENT_POI:
                this.showPoiFragment();
                break;
            case FRAGMENT_MAP:
                this.showMapFragment();
                break;
            case FRAGMENT_TRANSPORT:
                this.showTransportFragment();
                break;
            case FRAGMENT_SOCIAL:
                this.showSocialFragment();
                break;
            case FRAGMENT_EMERGENCY:
                this.showEmergencyFragment();
                break;
            case FRAGMENT_EMERGENCY_INFO:
                this.showEmergencyInfoFragment();
                break;
            case FRAGMENT_EMERGENCY_NUMBERS:
                this.showEmergencyNumbersFragment();
                break;
            case FRAGMENT_SETTINGS:
                this.showSettingsFragment();
                break;
            default:
                break;
        }
    }

    private void showHomeFragment() {
        this.fragmentHome = WelcomeFragment.newInstance();
        this.startTransactionFragment(this.fragmentHome, "HOME");
    }

    private void showInfoFragment() {
        this.fragmentInfo = FestivalInformationFragment.newInstance();
        this.startTransactionFragment(this.fragmentInfo, "INFO");
    }

    private void showScheduleFragment() {
        this.fragmentSchedule = ScheduleFragment.newInstance();
        Intent intent = new Intent(this, ConcertFlow.class);
        intent.setAction(FlowUtil.GET_ALL_CONCERT);
        intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent(this, RootActivity.class));
        startService(intent);
    }

    private void showPoiFragment() {
        this.fragmentPoi = PointOfInterestFragment.newInstance();
        this.startTransactionFragment(this.fragmentPoi, "POI");
    }

    private void showMapFragment() {
        this.fragmentMap = MapViewFragment.newInstance();
        this.startTransactionFragment(this.fragmentMap, MapViewFragment.TAG);
    }

    private void showTransportFragment() {
        this.fragmentTransport = TransportFragment.newInstance();
        this.startTransactionFragment(this.fragmentTransport, "TRANSPORT");
    }

    private void showSocialFragment() {
        this.fragmentSocial = SocialFragment.newInstance();
        this.startTransactionFragment(this.fragmentSocial, "SOCIAL");
    }

    private void showEmergencyFragment() {
        this.fragmentEmergency = EmergencyFragment.newInstance();
        this.startTransactionFragment(this.fragmentEmergency, "EMERGENCY");
    }

    private void showEmergencyInfoFragment() {
        this.fragmentEmergencyInfo = EmergencyInfoFragment.newInstance();
        this.startTransactionFragment(this.fragmentEmergencyInfo, "EMERGENCY_INFO");
    }

    private void showEmergencyNumbersFragment() {
        this.fragmentEmergencyNumbers = EmergencyNumbersFragment.newInstance();
        this.startTransactionFragment(this.fragmentEmergencyNumbers, "EMERGENCY_NUMBERS");
    }

    private void showSettingsFragment() {
        this.fragmentSettings = SettingsMainFragment.newInstance();
        this.startTransactionFragment(this.fragmentSettings, "SETTINGS");
    }

    private void startTransactionFragment(Fragment fragment, String tag) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_activity_frame_layout, fragment, tag)
                    .addToBackStack(tag).commit();
        }
    }

    private void showFirstFragment() {
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.root_activity_frame_layout);
        if (visibleFragment == null) {
            this.showFragment(FRAGMENT_HOME);
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    protected void configureToolBar(int toolbar_id) {
        this.toolbar = findViewById(toolbar_id);
        setSupportActionBar(toolbar);
    }

    protected void configureDrawerLayout(int drawer_id) {
        this.drawerLayout = findViewById(drawer_id);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    protected void configureNavigationView(int nav_id) {
        this.navigationView = findViewById(nav_id);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
    }

    protected void signOut() {
        getAppDatabaseWrapper().unregisterListeners();
        getAppAuthenticator().signOut();
        if (isLocationActive())
            disableLocation();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE) {
            fragmentHome.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}