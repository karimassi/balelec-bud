package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;

import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.intents.FlowUtil;
import ch.epfl.balelecbud.util.intents.IntentLauncher;

public class ScheduleActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ScheduleAdapter mAdapter;

    private static final String TAG = ScheduleAdapter.class.getSimpleName();

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

        this.configureToolBar(R.id.schedule_activity_toolbar);
        this.configureDrawerLayout(R.id.schedule_activity_drawer_layout);
        this.configureNavigationView(R.id.schedule_activity_nav_view);
    }

    @VisibleForTesting
    public void setIntentLauncher(IntentLauncher intentLauncher) {
        this.mAdapter.setIntentLauncher(intentLauncher);
    }

}
