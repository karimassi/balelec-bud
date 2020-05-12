package ch.epfl.balelecbud.view.transport;


import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.utility.TransportUtils;
import ch.epfl.balelecbud.model.TransportStation;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

public class TransportStationData extends RecyclerViewData<TransportStation, TransportStationHolder> {

    private Location userLocation;
    private OnRecyclerViewInteractionListener<TransportStation> interactionListener;

    public TransportStationData(Location userLocation, OnRecyclerViewInteractionListener<TransportStation> interactionListener) {
        this.userLocation = userLocation;
        this.interactionListener = interactionListener;
    }

    @Override
    public void reload(Database.Source preferredSource) {
        TransportUtils.getNearbyStations(userLocation)
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
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
