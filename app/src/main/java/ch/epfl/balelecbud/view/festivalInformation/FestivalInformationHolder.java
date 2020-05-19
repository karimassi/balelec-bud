package ch.epfl.balelecbud.view.festivalInformation;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class FestivalInformationHolder extends RecyclerView.ViewHolder {

    final TextView informationTitleTextView;
    final TextView informationContentTextView;

    FestivalInformationHolder(View view) {
        super(view);
        informationTitleTextView = view.findViewById(R.id.festivalInfoTitle);
        informationContentTextView = view.findViewById(R.id.festivalInfoContent);
    }
}