package ch.epfl.balelecbud.FestivalInformation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.balelecbud.R;

public class FestivalInformationAdapter extends RecyclerView.Adapter<FestivalInformationAdapter.FestivalInformationHolder> {

    private List<FestivalInformation> informationData;

    public FestivalInformationAdapter(List<FestivalInformation> informationList) {
        this.informationData = informationList;
    }

    @Override
    public FestivalInformationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_festival_info, parent, false);
        return new FestivalInformationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FestivalInformationHolder holder, int position) {
        holder.informationTitleTextView.setText(informationData.get(position).getTitle());
        holder.informationContentTextView.setText(informationData.get(position).getInformation());
    }

    @Override
    public int getItemCount() {
        return informationData.size();
    }

    public static class FestivalInformationHolder extends RecyclerView.ViewHolder {

        public TextView informationTitleTextView;
        public TextView informationContentTextView;

        public FestivalInformationHolder(View view) {
            super(view);
            informationTitleTextView = view.findViewById(R.id.festivalInfoTitle);
            informationContentTextView = view.findViewById(R.id.festivalInfoContent);
        }
    }
}