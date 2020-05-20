package ch.epfl.balelecbud.view.transport;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class TransportStationHolder extends RecyclerView.ViewHolder {

    final TextView nameView;
    final TextView distanceView;

    TransportStationHolder(View view) {
        super(view);
        nameView = view.findViewById(R.id.text_view_station_name);
        distanceView = view.findViewById(R.id.text_view_station_distance);
    }
}
