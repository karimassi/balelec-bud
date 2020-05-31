package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ch.epfl.balelecbud.view.ConnectivityFragment;
import ch.epfl.balelecbud.view.NoConnectionFragment;
import ch.epfl.balelecbud.view.gallery.GalleryFragment;
import ch.epfl.balelecbud.model.Slot;
import ch.epfl.balelecbud.utility.FlowUtils;
import ch.epfl.balelecbud.utility.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.view.welcome.WelcomeFragment;
import ch.epfl.balelecbud.view.emergency.EmergencyInformationFragment;
import ch.epfl.balelecbud.view.festivalInformation.FestivalInformationFragment;
import ch.epfl.balelecbud.view.friendship.SocialFragment;
import ch.epfl.balelecbud.view.map.MapViewFragment;
import ch.epfl.balelecbud.view.playlist.PlaylistFragment;
import ch.epfl.balelecbud.view.pointOfInterest.PointOfInterestFragment;
import ch.epfl.balelecbud.view.schedule.ScheduleFragment;
import ch.epfl.balelecbud.view.settings.SettingsFragment;
import ch.epfl.balelecbud.view.transport.TransportFragment;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

/**
 * Root activity used to display all the different fragments
 */
public final class RootActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = RootActivity.class.getSimpleName();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: created successfully");
        setContentView(R.layout.activity_root);
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        setUpUser();

        ArrayList<Slot> slots = FlowUtils.unpackCallback(getIntent());
        if (slots != null) {
            startTransactionFragment(ScheduleFragment.newInstance(slots), "SCHEDULE");
        } else {
            this.showFirstFragment();
        }
    }

    private void setUpUser() {
        if (getAppAuthenticator().getCurrentUser() == null || getAppAuthenticator().getCurrentUid() == null) {
            Log.d(TAG, "setUpUser: creating anonymous user");
            getAppAuthenticator().signInAnonymously();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.fragment_main_drawer_home:
                this.showHomeFragment();
                break;
            case R.id.fragment_main_drawer_info:
                this.showInfoFragment();
                break;
            case R.id.fragment_main_drawer_schedule:
                this.showScheduleFragment();
                break;
            case R.id.fragment_main_drawer_poi:
                this.showPoiFragment();
                break;
            case R.id.fragment_main_drawer_map:
                this.showMapFragment();
                break;
            case R.id.fragment_main_drawer_transport:
                this.showTransportFragment();
                break;
            case R.id.fragment_main_drawer_social:
                this.showSocialFragment();
                break;
            case R.id.fragment_main_drawer_playlist:
                this.showPlaylistFragment();
                break;
            case R.id.fragment_main_drawer_emergency_info:
                this.showEmergencyInfoFragment();
                break;
            case R.id.fragment_main_drawer_settings:
                this.showSettingsFragment();
                break;
            case R.id.fragment_main_drawer_gallery:
                this.showGalleryFragment();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeFragment() {
        startTransactionFragment(WelcomeFragment.newInstance(), "HOME");
    }

    private void showInfoFragment() {
        startTransactionFragment(FestivalInformationFragment.newInstance(), "INFO");
    }

    private void showScheduleFragment() {
        Intent intent = new Intent(this, ConcertFlow.class);
        intent.setAction(FlowUtils.GET_ALL_CONCERT);
        intent.putExtra(FlowUtils.CALLBACK_INTENT, new Intent(this, RootActivity.class));
        startService(intent);
    }

    private void showPoiFragment() {
        startTransactionFragment(PointOfInterestFragment.newInstance(), "POI");
    }

    private void showMapFragment() {
        startTransactionFragment(MapViewFragment.newInstance(), MapViewFragment.TAG);
    }

    private void showTransportFragment() {
        startTransactionFragment(TransportFragment.newInstance(), "TRANSPORT");
    }

    private void showSocialFragment() {
        if (getAppAuthenticator().getCurrentUser() == null) {
            Toast.makeText(this, R.string.require_sign_in, Toast.LENGTH_LONG).show();
        } else {
            startTransactionFragment(SocialFragment.newInstance(), "SOCIAL");
        }
    }

    private void showPlaylistFragment() {
        startTransactionFragment(PlaylistFragment.newInstance(), "PLAYLIST");
    }

    private void showEmergencyInfoFragment() {
        startTransactionFragment(EmergencyInformationFragment.newInstance(), "EMERGENCY_INFO");
    }

    private void showSettingsFragment() {
        startTransactionFragment(SettingsFragment.newInstance(), SettingsFragment.TAG);
    }

    private void showGalleryFragment() {
        startTransactionFragment(GalleryFragment.newInstance(), GalleryFragment.TAG);
    }

    private void startTransactionFragment(Fragment fragment, String tag) {
        if (!fragment.isVisible()) {
            if ((fragment instanceof ConnectivityFragment) && !((ConnectivityFragment) fragment).canBeDisplayed()) {
                fragment = NoConnectionFragment.newInstance();
                tag = "NO_CONNECTION";
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_activity_frame_layout, fragment, tag)
                    .addToBackStack(tag).commit();
        }
    }

    private void showFirstFragment() {
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.root_activity_frame_layout);
        if (visibleFragment == null) {
            this.showHomeFragment();
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private void configureToolBar() {
        this.toolbar = findViewById(R.id.root_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.root_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.root_activity_nav_view);
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
}