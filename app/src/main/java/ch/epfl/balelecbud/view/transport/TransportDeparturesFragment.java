package ch.epfl.balelecbud.view.transport;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.TransportDeparture;
import ch.epfl.balelecbud.model.TransportStation;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

public final class TransportDeparturesFragment extends Fragment {

    private static final String STATION_KEY = "station";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_transport_departures, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_transport_departures);
        View freshnessView = view.findViewById(R.id.freshness_info_layout);


        TransportStation station = getArguments().getParcelable(STATION_KEY);

        ((TextView)view.findViewById(R.id.text_view_transport_departures_title)).setText(station.getStationName());

        TransportDepartureData data = new TransportDepartureData(station);
        final RefreshableRecyclerViewAdapter<TransportDeparture, TransportDepartureHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(TransportDepartureHolder::new, freshnessView, data, R.layout.item_transport_departure);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh_layout_transport_departures);
        adapter.setOnRefreshListener(refreshLayout);

        return view;
    }

    public static TransportDeparturesFragment newInstance(TransportStation station) {
        Bundle args = new Bundle();
        args.putParcelable(STATION_KEY, station);
        TransportDeparturesFragment fragment = new TransportDeparturesFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
