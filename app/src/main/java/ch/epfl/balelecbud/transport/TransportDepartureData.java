package ch.epfl.balelecbud.transport;


import ch.epfl.balelecbud.transport.objects.TransportDeparture;
import ch.epfl.balelecbud.transport.objects.TransportStation;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class TransportDepartureData extends RecyclerViewData<TransportDeparture, TransportDepartureHolder> {

    private TransportStation station;

    public TransportDepartureData(TransportStation station) {
        this.station = station;
    }

    @Override
    public void reload() {
        TransportUtil.getNextDepartures(station)
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, TransportDepartureHolder viewHolder) {
        viewHolder.lineView.setText(data.get(index).getLine());
        viewHolder.destinationView.setText(data.get(index).getDestination());
        viewHolder.departureTimeView.setText(data.get(index).getTimeString());
    }
}
