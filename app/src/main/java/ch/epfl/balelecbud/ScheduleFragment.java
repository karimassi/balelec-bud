package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ScheduleFragment extends Fragment {
    
    private static final String TAG = ScheduleFragment.class.getSimpleName();
    private FragmentActivity activity;
    private List<Slot> slots;

    public static ScheduleFragment newInstance() {
        return (new ScheduleFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_schedule, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        activity = this.getActivity();
        Log.v(TAG, "onCreate: Creation of the activity");

        RecyclerView rvSchedule = getView().findViewById(R.id.scheduleRecyclerView);

        ScheduleAdapter mAdapter = new ScheduleAdapter(activity, slots);
        rvSchedule.setLayoutManager(new LinearLayoutManager(activity));
        rvSchedule.setHasFixedSize(true);
        rvSchedule.setAdapter(mAdapter);
    }

    public void setSlots(List<Slot> slots){
        this.slots = slots;
    }
}
