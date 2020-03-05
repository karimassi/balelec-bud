package ch.epfl.balelecbud.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ScheduleActivity extends AppCompatActivity{
    private ArrayList<Slot> schedule;

    private AbstractScheduleProvider scheduleProvider;
    private ScheduleAdapter mAdapter;
    private RecyclerView rvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        rvSchedule = findViewById(R.id.rvSchedule);

        mAdapter = new ScheduleAdapter(schedule, scheduleProvider);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(mAdapter);
    }
}
