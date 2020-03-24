package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import ch.epfl.balelecbud.notifications.concertFlow.FlowUtil;
import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ScheduleActivity extends BasicActivity {
    private static final String TAG = ScheduleAdapter.class.getSimpleName();
    private ScheduleAdapter mAdapter;
    private RecyclerView rvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Log.d(TAG, "onCreate: Creation of the activity");
        rvSchedule = findViewById(R.id.scheduleRecyclerView);
        List<Slot> slots = FlowUtil.unpackCallback(getIntent());
        mAdapter = new ScheduleAdapter(this, slots == null ? Collections.<Slot>emptyList():slots);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(mAdapter);
    }

    @VisibleForTesting
    public void setServiceInterface(ScheduleAdapter.ServiceInterface serviceInterface) {
        this.mAdapter.setService(serviceInterface);
    }
}
