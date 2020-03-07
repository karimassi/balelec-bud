package ch.epfl.balelecbud.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import ch.epfl.balelecbud.R;

public class ScheduleActivity extends AppCompatActivity{
    private AbstractScheduleProvider scheduleProvider;
    private ScheduleAdapter mAdapter;
    private RecyclerView rvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        this.scheduleProvider = new FirebaseRealtimeScheduleProvider("oui");

        rvSchedule = findViewById(R.id.rvSchedule);
        mAdapter = new ScheduleAdapter(scheduleProvider);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(mAdapter);
    }
}
