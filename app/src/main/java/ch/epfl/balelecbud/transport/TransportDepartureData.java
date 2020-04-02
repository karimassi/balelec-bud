package ch.epfl.balelecbud.transport;


import android.util.Log;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.transport.objects.TransportStation;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class TransportStationData extends RecyclerViewData<TransportStation, TransportStationHolder> {

    Location userLocation;

    public TransportStationData(Location userLocation) {
        this.userLocation = userLocation;
    }

    @Override
    public void reload() {
        new TransportService().getNearbyStations(userLocation)
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, TransportStationHolder viewHolder) {
        viewHolder.nameView.setText(data.get(index).getStationName());
        viewHolder.distanceView.setText(String.valueOf(data.get(index).getDistanceToUser()));
    }
}
