package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.notifications.concertFlow.AbstractConcertFlow;
import ch.epfl.balelecbud.schedule.ScheduleAdapter;

public class ScheduleActivity extends BasicActivity {
    private static final String TAG = ScheduleAdapter.class.getSimpleName();
    private ScheduleAdapter mAdapter;
    private RecyclerView rvSchedule;
    private AbstractConcertFlow flow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Log.d(TAG, "onCreate: Creation of the activity");
        rvSchedule = findViewById(R.id.scheduleRecyclerView);
        mAdapter = new ScheduleAdapter(this);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
