package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ch.epfl.balelecbud.view.gallery.GalleryFragment;
import ch.epfl.balelecbud.model.Slot;
import ch.epfl.balelecbud.utility.FlowUtils;
import ch.epfl.balelecbud.utility.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.view.PicturesFragment;
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
    private Fragment fragmentHome = WelcomeFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();

        setupUser();

        ArrayList<Slot> slots = FlowUtils.unpackCallback(getIntent());
        if (slots != null) {
            ScheduleFragment fragmentSchedule = ScheduleFragment.newInstance(slots);
            if (!fragmentSchedule.isVisible()) {
                startTransactionFragment(fragmentSchedule, ScheduleFragment.TAG);
            }
        } else {
            showFirstFragment();
        }

        Log.d(WelcomeFragment.TAG, "RootActivityCreate: trying to restore last saved instance");
        if(savedInstanceState != null) {
            fragmentHome = getSupportFragmentManager().getFragment(savedInstanceState, WelcomeFragment.TAG);
            Log.d(WelcomeFragment.TAG, "RootActivityCreate: successfully restored fragment");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(WelcomeFragment.TAG, "RootActivitySave: save instance state");
        getSupportFragmentManager().putFragment(outState, WelcomeFragment.TAG, fragmentHome);
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
            case R.id.fragment_main_drawer_pictures:
                showPicturesFragment();
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
        startTransactionFragment(fragmentHome, WelcomeFragment.TAG);
    }

    private void showInfoFragment() {
        Fragment fragmentInfo = FestivalInformationFragment.newInstance();
        startTransactionFragment(fragmentInfo, FestivalInformationFragment.TAG);
    }

    private void showScheduleFragment() {
        Intent intent = new Intent(this, ConcertFlow.class);
        intent.setAction(FlowUtils.GET_ALL_CONCERT);
        intent.putExtra(FlowUtils.CALLBACK_INTENT, new Intent(this, RootActivity.class));
        startService(intent);
    }

    private void showPoiFragment() {
        Fragment fragmentPoi = PointOfInterestFragment.newInstance();
        startTransactionFragment(fragmentPoi, PointOfInterestFragment.TAG);
    }

    private void showMapFragment() {
        MapViewFragment fragmentMap = MapViewFragment.newInstance();
        startTransactionFragment(fragmentMap, MapViewFragment.TAG);
    }

    private void showTransportFragment() {
        Fragment fragmentTransport = TransportFragment.newInstance();
        startTransactionFragment(fragmentTransport, TransportFragment.TAG);
    }

    private void showSocialFragment() {
        if (getAppAuthenticator().getCurrentUser() == null) {
            Toast.makeText(this, R.string.require_sign_in, Toast.LENGTH_LONG).show();
        } else {
            Fragment fragmentSocial = SocialFragment.newInstance();
            startTransactionFragment(fragmentSocial, SocialFragment.TAG);
        }
    }

    private void showPlaylistFragment() {
        Fragment fragmentPlaylist = PlaylistFragment.newInstance();
        startTransactionFragment(fragmentPlaylist, PlaylistFragment.TAG);
    }

    private void showEmergencyInfoFragment() {
        Fragment fragmentEmergencyInfo = EmergencyInformationFragment.newInstance();
        startTransactionFragment(fragmentEmergencyInfo, EmergencyInformationFragment.TAG);
    }

    private void showSettingsFragment() {
        Fragment fragmentSettings = SettingsFragment.newInstance();
        startTransactionFragment(fragmentSettings, SettingsFragment.TAG);
    }

    private void showGalleryFragment() {
        GalleryFragment fragmentGallery = GalleryFragment.newInstance();
        startTransactionFragment(fragmentGallery, GalleryFragment.TAG);
    }

    private void showPicturesFragment() {
        Fragment fragmentPictures = PicturesFragment.newInstance();
        startTransactionFragment(fragmentPictures, PicturesFragment.TAG);
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