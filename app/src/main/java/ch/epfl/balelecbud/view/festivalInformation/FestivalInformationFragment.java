package ch.epfl.balelecbud.view.festivalInformation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.FestivalInformation;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

public final class FestivalInformationFragment extends Fragment {

    public static final String TAG = FestivalInformationFragment.class.getSimpleName();

    public static FestivalInformationFragment newInstance() {
        return new FestivalInformationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_festival_info, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        RecyclerView recyclerView = getActivity().findViewById(R.id.festivalInfoRecyclerView);
        SwipeRefreshLayout refreshLayout = getView().findViewById(R.id.swipe_refresh_layout_festival_info);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        RecyclerViewData<FestivalInformation, FestivalInformationHolder> data = new FestivalInformationData();
        RefreshableRecyclerViewAdapter<FestivalInformation, FestivalInformationHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(FestivalInformationHolder::new, refreshLayout, data, R.layout.item_festival_info);
        recyclerView.setAdapter(adapter);

        adapter.setOnRefreshListener(refreshLayout);
    }
}