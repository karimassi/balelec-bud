package ch.epfl.balelecbud.view.schedule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Slot;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

public class ScheduleFragment extends Fragment {
    
    private static final String TAG = ScheduleFragment.class.getSimpleName();

    public static ScheduleFragment newInstance(ArrayList<Slot> subscribedSlots) {
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList("slots", subscribedSlots);
        scheduleFragment.setArguments(arguments);
        return scheduleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG, "onCreate: Creation of the activity");
        RecyclerView rvSchedule = getView().findViewById(R.id.scheduleRecyclerView);
        View freshnessView = getView().findViewById(R.id.freshness_info_layout);

        SlotData data = new SlotData(getActivity(), getArguments().getParcelableArrayList("slots"));
        RefreshableRecyclerViewAdapter<Slot, SlotHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                SlotHolder::new, freshnessView, data, R.layout.item_schedule);

        rvSchedule.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSchedule.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_schedule);
        adapter.setOnRefreshListener(refreshLayout);
    }
}
