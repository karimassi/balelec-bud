package ch.epfl.balelecbud;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class BasicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Authenticator authenticator = FirebaseAuthenticator.getInstance();
    private static DatabaseWrapper databaseWrapper = FirestoreDatabaseWrapper.getInstance();

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.activity_main_drawer_info:
                startActivity(new Intent(this, FestivalInformationActivity.class));
                break;
            case R.id.activity_main_drawer_schedule:
                startActivity(new Intent(this, ScheduleActivity.class));
                break;
            case R.id.activity_main_drawer_poi:
                startActivity(new Intent(this, PointOfInterestActivity.class));
                break;
            case R.id.activity_main_drawer_map:
                startActivity(new Intent(this, MapViewActivity.class));
                break;
            case R.id.activity_main_drawer_transport:
                startActivity(new Intent(this, TransportActivity.class));
                break;
            case R.id.activity_main_drawer_social:
                startActivity(new Intent(this, SocialActivity.class));
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

    private void signOut() {
        getAuthenticator().signOut();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }

    @VisibleForTesting
    public static void setAuthenticator(Authenticator auth) {
        authenticator = auth;
    }

    @VisibleForTesting
    public static void setDatabase(DatabaseWrapper db) {
        databaseWrapper = db;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public DatabaseWrapper getDatabase() {
        return databaseWrapper;
    }
}
