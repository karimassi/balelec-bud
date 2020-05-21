package ch.epfl.balelecbud.view.transport;


import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.TransportDeparture;
import ch.epfl.balelecbud.model.TransportStation;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.TransportUtils;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

public final class TransportDepartureData extends RecyclerViewData<TransportDeparture, TransportDepartureHolder> {

    private TransportStation station;

    TransportDepartureData(TransportStation station) {
        this.station = station;
    }

    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        return TransportUtils.getNextDepartures(station)
                //wrap in FetchedData with freshness set to null
                .thenApply(FetchedData::new)
                .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
    }

    @Override
    public void bind(int index, TransportDepartureHolder viewHolder) {
        viewHolder.lineView.setText(data.get(index).getLine());
        viewHolder.destinationView.setText(data.get(index).getDestination());
        viewHolder.departureTimeView.setText(data.get(index).getTimeString());
    }
}
