package ch.epfl.balelecbud.view.transport;


import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.TransportStation;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.TransportUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

public final class TransportStationData extends RecyclerViewData<TransportStation, TransportStationHolder> {

    private Location userLocation;
    private OnRecyclerViewInteractionListener<TransportStation> interactionListener;

    TransportStationData(Location userLocation, OnRecyclerViewInteractionListener<TransportStation> interactionListener) {
        this.userLocation = userLocation;
        this.interactionListener = interactionListener;
    }

    @Override
    public CompletableFuture<Long> reload(Database.Source preferredSource) {
        return TransportUtils.getNearbyStations(userLocation)
                //wrap in FetchedData with freshness set to null
                .thenApply(FetchedData::new)
                .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
    }

    @Override
    public void bind(int index, TransportStationHolder viewHolder) {
        viewHolder.nameView.setText(data.get(index).getStationName());
        viewHolder.distanceView.setText(String.valueOf(data.get(index).getDistanceToUser()));
        viewHolder.itemView.setOnClickListener(v -> {
            interactionListener.onItemSelected(data.get(index));
        });
    }
}
