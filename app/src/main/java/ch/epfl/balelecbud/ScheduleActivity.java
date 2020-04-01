package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
=======
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
>>>>>>> master

import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.intents.FlowUtil;
import ch.epfl.balelecbud.util.intents.IntentLauncher;

<<<<<<< HEAD
public class ScheduleActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ScheduleAdapter mAdapter;
    private RecyclerView rvSchedule;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
=======
public class ScheduleActivity extends BasicActivity {
    private static final String TAG = ScheduleAdapter.class.getSimpleName();
    private ScheduleAdapter mAdapter;
>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Log.d(TAG, "onCreate: Creation of the activity");
        RecyclerView rvSchedule = findViewById(R.id.scheduleRecyclerView);
        List<Slot> slots = FlowUtil.unpackCallback(getIntent());
        mAdapter = new ScheduleAdapter(this, slots == null ?
                Collections.<Slot>emptyList() : slots);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(mAdapter);

        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
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
        this.toolbar = (Toolbar) findViewById(R.id.schedule_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.schedule_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.schedule_activity_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @VisibleForTesting
    public void setIntentLauncher(IntentLauncher intentLauncher) {
        this.mAdapter.setIntentLauncher(intentLauncher);
    }
}
