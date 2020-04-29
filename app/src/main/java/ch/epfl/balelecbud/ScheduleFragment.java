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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import ch.epfl.balelecbud.schedule.SlotData;
import ch.epfl.balelecbud.schedule.SlotHolder;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

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
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        activity = this.getActivity();
        Log.v(TAG, "onCreate: Creation of the activity");
        RecyclerView rvSchedule = getView().findViewById(R.id.scheduleRecyclerView);

        SlotData data = new SlotData(getActivity(), slots);
        RefreshableRecyclerViewAdapter<Slot, SlotHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                SlotHolder::new, data, R.layout.item_schedule);

        rvSchedule.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSchedule.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_schedule);
        adapter.setOnRefreshListener(refreshLayout);
    }

    public void setSlots(List<Slot> slots){
        this.slots = slots;
    }
}
