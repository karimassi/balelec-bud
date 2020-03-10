package ch.epfl.balelecbud.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import ch.epfl.balelecbud.R;

public class ScheduleActivity extends AppCompatActivity{
    private AbstractScheduleProvider scheduleProvider;
    private ScheduleAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String db = getIntent().getStringExtra("db");
        if(db == "mock"){
            scheduleProvider = new MockScheduleProvider();
        } else {
            scheduleProvider = new FirebaseRealtimeScheduleProvider();
        }
       // scheduleProvider = new MockScheduleProvider();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        recyclerView = findViewById(R.id.rvSchedule);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ScheduleAdapter(scheduleProvider);
        recyclerView.setAdapter(mAdapter);
    }
}
