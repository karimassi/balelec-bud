package ch.epfl.balelecbud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import ch.epfl.balelecbud.emergency.EmergencyInfoData;
import ch.epfl.balelecbud.emergency.EmergencyInfoHolder;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class EmergencyInfoFragment extends Fragment {

    public static EmergencyInfoFragment newInstance() {
        return (new EmergencyInfoFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emergency_info, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        RecyclerView recyclerView = getActivity().findViewById(R.id.emergencyInfoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        RecyclerViewData<EmergencyInfo, EmergencyInfoHolder> data = new EmergencyInfoData();
        RefreshableRecyclerViewAdapter<EmergencyInfo, EmergencyInfoHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(EmergencyInfoHolder::new, data, R.layout.item_emergencyinfo);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_emergency_info);
        adapter.setOnRefreshListener(refreshLayout);
    }
}

