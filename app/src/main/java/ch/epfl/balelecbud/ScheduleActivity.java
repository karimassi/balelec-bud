package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.schedule.ScheduleAdapter;

public class ScheduleActivity extends BasicActivity {

    private ScheduleAdapter mAdapter;
    private RecyclerView rvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        rvSchedule = findViewById(R.id.scheduleRecyclerView);
        mAdapter = new ScheduleAdapter(new ConcertFlow(this));
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(mAdapter);
    }
}
