package ch.epfl.balelecbud.transport;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class TransportStationHolder extends RecyclerView.ViewHolder {

    public final TextView nameView;
    public final TextView distanceView;

    public TransportStationHolder(View view) {
        super(view);
        nameView = view.findViewById(R.id.text_view_station_name);
        distanceView = view.findViewById(R.id.text_view_station_distance);
    }
}
