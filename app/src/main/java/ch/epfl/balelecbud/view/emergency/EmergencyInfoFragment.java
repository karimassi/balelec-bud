package ch.epfl.balelecbud.view.emergency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.EmergencyInfo;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

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
        View freshnessView = getView().findViewById(R.id.freshness_info_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        RecyclerViewData<EmergencyInfo, EmergencyInfoHolder> data = new EmergencyInfoData();
        RefreshableRecyclerViewAdapter<EmergencyInfo, EmergencyInfoHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(EmergencyInfoHolder::new, freshnessView, data, R.layout.item_emergencyinfo);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_emergency_info);
        adapter.setOnRefreshListener(refreshLayout);
    }
}

