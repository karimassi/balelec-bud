package ch.epfl.balelecbud.festivalInformation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class FestivalInformationAdapter extends RecyclerView.Adapter<FestivalInformationAdapter.FestivalInformationHolder> {

    private List<FestivalInformation> informationData;
    private static DatabaseWrapper database = new FirestoreDatabaseWrapper();

    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper){
        database = databaseWrapper;
    }

    public FestivalInformationAdapter() {
        this.informationData = new ArrayList<>();
        RecyclerViewAdapterFacade facade = new RecyclerViewAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                FestivalInformationAdapter.this.notifyItemInserted(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                FestivalInformationAdapter.this.notifyItemChanged(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                FestivalInformationAdapter.this.notifyItemRemoved(position);
            }
        };
        DatabaseListener<FestivalInformation> listener = new DatabaseListener(facade, informationData, FestivalInformation.class);
        database.listen(DatabaseWrapper.FESITVAL_INFORMATION_PATH, listener);
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