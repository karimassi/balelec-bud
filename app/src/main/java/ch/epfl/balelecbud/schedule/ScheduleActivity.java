package ch.epfl.balelecbud.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import java.util.ArrayList;

import ch.epfl.balelecbud.R;

public class ScheduleActivity extends AppCompatActivity{
    ArrayList<Slot> schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        RecyclerView rvContacts = findViewById(R.id.rvSchedule);

        schedule = Slot.createSchedule(20);
        ScheduleAdapter adapter = new ScheduleAdapter(schedule);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);
    }
}
