package ch.epfl.balelecbud.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import ch.epfl.balelecbud.R;

public class ScheduleActivity extends AppCompatActivity{
    private ArrayList<Slot> schedule;

    private DatabaseReference mScheduleRef;
    private ValueEventListener mScheduleListener;

    private ScheduleAdapter mAdapter;
    private RecyclerView rvSchedule;

    private TextView timeSlotView;
    private TextView artistNameView;
    private TextView sceneNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //Initialize Database
        mScheduleRef = FirebaseDatabase.getInstance().getReference();

        rvSchedule = findViewById(R.id.rvSchedule);

        mAdapter = new ScheduleAdapter(schedule, mScheduleRef);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener scheduleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Slot slot = dataSnapshot.getValue(Slot.class);
                timeSlotView.setText(slot.getTimeSlot());
                artistNameView.setText(slot.getArtistName());
                sceneNameView.setText(slot.getSceneName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mScheduleRef.addValueEventListener(scheduleListener);
        mScheduleListener = scheduleListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mScheduleListener != null) {
            mScheduleRef.removeEventListener(mScheduleListener);
        }
        mAdapter.cleanupListener();
    }


}
