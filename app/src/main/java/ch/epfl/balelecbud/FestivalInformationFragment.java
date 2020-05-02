package ch.epfl.balelecbud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.festivalInformation.FestivalInformationData;
import ch.epfl.balelecbud.festivalInformation.FestivalInformationHolder;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class FestivalInformationFragment extends Fragment {

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // Not sure about this one
        recyclerView.setHasFixedSize(true);

        RecyclerViewData<FestivalInformation, FestivalInformationHolder> data = new FestivalInformationData();
        RefreshableRecyclerViewAdapter<FestivalInformation, FestivalInformationHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(FestivalInformationHolder::new, data, R.layout.item_festival_info);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = getView().findViewById(R.id.swipe_refresh_layout_festival_info);
        adapter.setOnRefreshListener(refreshLayout);
    }
}