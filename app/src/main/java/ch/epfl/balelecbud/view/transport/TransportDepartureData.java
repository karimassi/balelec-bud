package ch.epfl.balelecbud.view.transport;


import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.TransportDeparture;
import ch.epfl.balelecbud.model.TransportStation;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.TransportUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

public class TransportDepartureData extends RecyclerViewData<TransportDeparture, TransportDepartureHolder> {

    private TransportStation station;

    public TransportDepartureData(TransportStation station) {
        this.station = station;
    }

    @Override
    public CompletableFuture<Void> reload(Database.Source preferredSource) {
        return TransportUtils.getNextDepartures(station)
                .thenAccept(new CompletableFutureUtils.MergeConsumer<>(this));
    }

    @Override
    public void bind(int index, TransportDepartureHolder viewHolder) {
        viewHolder.lineView.setText(data.get(index).getLine());
        viewHolder.destinationView.setText(data.get(index).getDestination());
        viewHolder.departureTimeView.setText(data.get(index).getTimeString());
    }
}
