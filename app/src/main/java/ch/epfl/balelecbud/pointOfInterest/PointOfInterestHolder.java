package ch.epfl.balelecbud.pointOfInterest;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class PointOfInterestHolder extends RecyclerView.ViewHolder {

    public final TextView nameTextView;
    public final TextView typeTextView;
    public final TextView amountNearPoiTextView;

    public PointOfInterestHolder(View view) {
        super(view);
        nameTextView = view.findViewById(R.id.text_view_poi_name);
        typeTextView = view.findViewById(R.id.text_view_poi_type);
        amountNearPoiTextView = view.findViewById(R.id.text_view_poi_amout_nearby);
    }
}

