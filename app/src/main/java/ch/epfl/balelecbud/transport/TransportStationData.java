package ch.epfl.balelecbud.transport;


import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.transport.objects.TransportStation;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.views.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class TransportStationData extends RecyclerViewData<TransportStation, TransportStationHolder> {

    private Location userLocation;
    private OnRecyclerViewInteractionListener<TransportStation> interactionListener;

    public TransportStationData(Location userLocation, OnRecyclerViewInteractionListener<TransportStation> interactionListener) {
        this.userLocation = userLocation;
        this.interactionListener = interactionListener;
    }

    @Override
    public void reload() {
        TransportUtil.getNearbyStations(userLocation)
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
