package ch.epfl.balelecbud.friendship;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.balelecbud.BasicActivity;
import ch.epfl.balelecbud.FestivalInformationActivity;
import ch.epfl.balelecbud.LoginUserActivity;
import ch.epfl.balelecbud.MapViewActivity;
import ch.epfl.balelecbud.PointOfInterestActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.ScheduleActivity;
import ch.epfl.balelecbud.TransportActivity;

public class SocialActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SocialAdapter fragmentAdapter;
    ViewPager2 viewPager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private List<String> tabTitleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        tabTitleList = new ArrayList<>(Arrays.asList(getString(R.string.tab_friends), getString(R.string.tab_requests)));

        setupFragmentAdapter();
        viewPager = findViewById(R.id.view_pager_social);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabs = findViewById(R.id.tabs_social);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(tabTitleList.get(position))).attach();

        FloatingActionButton fabAddFriends = findViewById(R.id.fab_add_friends);
        fabAddFriends.setOnClickListener(v -> {
            AddFriendFragment dialog =AddFriendFragment.newInstance(getAuthenticator().getCurrentUser());
            dialog.show(getSupportFragmentManager(), getString(R.string.add_friend_title));
        });

    }

    private void setupFragmentAdapter() {
        fragmentAdapter = new SocialAdapter(getSupportFragmentManager(), getLifecycle());
        fragmentAdapter.addFragment(FriendsFragment.newInstance(getAuthenticator().getCurrentUser()));
        fragmentAdapter.addFragment(FriendRequestsFragment.newInstance(getAuthenticator().getCurrentUser()));
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
            case R.id.activity_main_drawer_social:
                Intent intentSocial = new Intent(this, SocialActivity.class);
                startActivity(intentSocial);
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
        this.toolbar = (Toolbar) findViewById(R.id.social_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.social_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.social_activity_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void signOut() {
        getAuthenticator().signOut();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }
}
