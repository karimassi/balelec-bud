package ch.epfl.balelecbud.festivalInformation;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class FestivalInformationHolder extends RecyclerView.ViewHolder {

    public TextView informationTitleTextView;
    public TextView informationContentTextView;

    public FestivalInformationHolder(View view) {
        super(view);
        informationTitleTextView = view.findViewById(R.id.festivalInfoTitle);
        informationContentTextView = view.findViewById(R.id.festivalInfoContent);
    }
}