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
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();

        setupUser();

        ArrayList<Slot> slots = FlowUtils.unpackCallback(getIntent());
        if (slots != null) {
            startTransactionFragment(ScheduleFragment.newInstance(slots), ScheduleFragment.TAG);
        } else {
            showFirstFragment();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.fragment_main_drawer_home:
                showHomeFragment();
                break;
            case R.id.fragment_main_drawer_info:
                showInfoFragment();
                break;
            case R.id.fragment_main_drawer_schedule:
                showScheduleFragment();
                break;
            case R.id.fragment_main_drawer_poi:
                showPoiFragment();
                break;
            case R.id.fragment_main_drawer_map:
                showMapFragment();
                break;
            case R.id.fragment_main_drawer_transport:
                showTransportFragment();
                break;
            case R.id.fragment_main_drawer_social:
                showSocialFragment();
                break;
            case R.id.fragment_main_drawer_playlist:
                showPlaylistFragment();
                break;
            case R.id.fragment_main_drawer_emergency_info:
                showEmergencyInfoFragment();
                break;
            case R.id.fragment_main_drawer_settings:
                showSettingsFragment();
                break;
            case R.id.fragment_main_drawer_gallery:
                showGalleryFragment();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void setupUser() {
        if (getAppAuthenticator().getCurrentUser() == null || getAppAuthenticator().getCurrentUid() == null) {
            Log.d(TAG, "setupUser: creating anonymous user");
            getAppAuthenticator().signInAnonymously();
        }
    }

    private void showHomeFragment() {
        startTransactionFragment(WelcomeFragment.newInstance(), WelcomeFragment.TAG);
    }

    private void showInfoFragment() {
        startTransactionFragment(FestivalInformationFragment.newInstance(), FestivalInformationFragment.TAG);
    }

    private void showScheduleFragment() {
        Intent intent = new Intent(this, ConcertFlow.class);
        intent.setAction(FlowUtils.GET_ALL_CONCERT);
        intent.putExtra(FlowUtils.CALLBACK_INTENT, new Intent(this, RootActivity.class));
        startService(intent);
    }

    private void showPoiFragment() {
        startTransactionFragment(PointOfInterestFragment.newInstance(), PointOfInterestFragment.TAG);
    }

    private void showMapFragment() {
        startTransactionFragment(MapViewFragment.newInstance(), MapViewFragment.TAG);
    }

    private void showTransportFragment() {
        startTransactionFragment(TransportFragment.newInstance(), TransportFragment.TAG);
    }

    private void showSocialFragment() {
        if (getAppAuthenticator().getCurrentUser() == null) {
            Toast.makeText(this, R.string.require_sign_in, Toast.LENGTH_LONG).show();
        } else {
            startTransactionFragment(SocialFragment.newInstance(), SocialFragment.TAG);
        }
    }

    private void showPlaylistFragment() {
        startTransactionFragment(PlaylistFragment.newInstance(), PlaylistFragment.TAG);
    }

    private void showEmergencyInfoFragment() {
        startTransactionFragment(EmergencyInformationFragment.newInstance(), EmergencyInformationFragment.TAG);
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
                tag = NoConnectionFragment.TAG;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_activity_frame_layout, fragment, tag)
                    .addToBackStack(tag).commit();
        }
    }

    private void showFirstFragment() {
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.root_activity_frame_layout);
        if (visibleFragment == null) {
            showHomeFragment();
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private void configureToolBar() {
        toolbar = findViewById(R.id.root_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout() {
        drawerLayout = findViewById(R.id.root_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationView = findViewById(R.id.root_activity_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}