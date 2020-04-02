package ch.epfl.balelecbud.transport;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.transport.objects.TransportStation;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class TransportSationFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_transport_station, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_transport);
        Location userLocation = new Location(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
        TransportStationData data = new TransportStationData(userLocation);
        final RefreshableRecyclerViewAdapter<TransportStation, TransportStationHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(TransportStationHolder::new, data, R.layout.item_transport_station);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh_layout_transport);
        adapter.setOnRefreshListener(refreshLayout);

        return view;
    }

    public static TransportSationFragment newInstance(Location userLocation) {
        Bundle args = new Bundle();
        args.putDouble("latitude", userLocation.getLatitude());
        args.putDouble("longitude", userLocation.getLongitude());
        TransportSationFragment fragment = new TransportSationFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
