package ch.epfl.balelecbud.transport;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class TransportDepartureHolder extends RecyclerView.ViewHolder {

    public final TextView lineView;
    public final TextView destinationView;
    public final TextView departureTimeView;

    public TransportDepartureHolder(View view) {
        super(view);
        lineView = view.findViewById(R.id.text_view_departure_line);
        destinationView = view.findViewById(R.id.text_view_departure_destination);
        departureTimeView = view.findViewById(R.id.text_view_departure_time);
    }
}
