package ch.epfl.balelecbud.pointOfInterest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class PointOfInterestFragment extends Fragment {

    private FragmentActivity activity;

    public static PointOfInterestFragment newInstance() {
        return (new PointOfInterestFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_point_of_interest, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = this.getActivity();

        RecyclerView recyclerView = getView().findViewById(R.id.pointOfInterestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);
        RecyclerViewData<PointOfInterest, PointOfInterestHolder> data = new PointOfInterestData(getActivity());
        RefreshableRecyclerViewAdapter<PointOfInterest, PointOfInterestHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                PointOfInterestHolder::new, data, R.layout.item_point_of_interest);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = getView().findViewById(R.id.swipe_refresh_layout_point_of_interest);
        adapter.setOnRefreshListener(refreshLayout);
    }

}
