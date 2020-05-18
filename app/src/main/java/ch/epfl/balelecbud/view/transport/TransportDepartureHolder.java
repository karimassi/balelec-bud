package ch.epfl.balelecbud.view.transport;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class TransportDepartureHolder extends RecyclerView.ViewHolder {

    final TextView lineView;
    final TextView destinationView;
    final TextView departureTimeView;

    TransportDepartureHolder(View view) {
        super(view);
        lineView = view.findViewById(R.id.text_view_departure_line);
        destinationView = view.findViewById(R.id.text_view_departure_destination);
        departureTimeView = view.findViewById(R.id.text_view_departure_time);
    }
}
