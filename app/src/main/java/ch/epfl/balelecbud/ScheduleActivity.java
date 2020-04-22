package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.SlotData;
import ch.epfl.balelecbud.schedule.SlotHolder;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.intents.FlowUtil;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class ScheduleActivity extends BasicActivity {
    
    private static final String TAG = ScheduleAdapter.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        this.configureToolBar(R.id.schedule_activity_toolbar);
        this.configureDrawerLayout(R.id.schedule_activity_drawer_layout);
        this.configureNavigationView(R.id.schedule_activity_nav_view);

        Log.v(TAG, "onCreate: Creation of the activity");
        RecyclerView rvSchedule = findViewById(R.id.scheduleRecyclerView);
        List<Slot> slots = FlowUtil.unpackCallback(getIntent());

        SlotData data = new SlotData(this, slots);
        RefreshableRecyclerViewAdapter<Slot, SlotHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                SlotHolder::new, data, R.layout.item_schedule);

        //mAdapter = new ScheduleAdapter(this, slots);

        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout_schedule);
        adapter.setOnRefreshListener(refreshLayout);
    }

}
