package ch.epfl.balelecbud;

import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.friendship.SocialFragment;
import ch.epfl.balelecbud.map.MapViewFragment;
import ch.epfl.balelecbud.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.util.intents.FlowUtil;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.location.LocationUtil.disableLocation;
import static ch.epfl.balelecbud.location.LocationUtil.isLocationActive;

public abstract class BasicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.activity_main_drawer_info:
                startActivity(new Intent(this, FestivalInformationFragment.class));
                break;
            case R.id.activity_main_drawer_schedule:
                Intent intent = new Intent(this, ConcertFlow.class);
                intent.setAction(FlowUtil.GET_ALL_CONCERT);
                intent.putExtra(FlowUtil.CALLBACK_INTENT, new Intent(this, RootActivity.class));
                startService(intent);
                break;
            case R.id.activity_main_drawer_poi:
                startActivity(new Intent(this, PointOfInterestFragment.class));
                break;
            case R.id.activity_main_drawer_map:
                startActivity(new Intent(this, MapViewFragment.class));
                break;
            case R.id.activity_main_drawer_transport:
                startActivity(new Intent(this, TransportFragment.class));
                break;
            case R.id.activity_main_drawer_social:
                startActivity(new Intent(this, SocialFragment.class));
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
        }
    }

    protected void signOut() {
        getAppAuthenticator().signOut();
        if (isLocationActive())
            disableLocation();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }
}
