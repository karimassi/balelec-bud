package ch.epfl.balelecbud.models;

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
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;

public class PointOfInterestAdapter extends RecyclerView.Adapter<PointOfInterestAdapter
        .PointOfInterestHolder>{

    private List<PointOfInterest> pointOfInterestData;
    private static DatabaseWrapper database = FirestoreDatabaseWrapper.getInstance();

    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper) {
        database = databaseWrapper;
    }

    public PointOfInterestAdapter() {
        this.pointOfInterestData = new ArrayList<>();
        RecyclerViewAdapterFacade facade = new RecyclerViewAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                PointOfInterestAdapter.this.notifyItemInserted(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                PointOfInterestAdapter.this.notifyItemChanged(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                PointOfInterestAdapter.this.notifyItemRemoved(position);
            }
        };
        DatabaseListener<PointOfInterest> listener = new DatabaseListener<>(facade, pointOfInterestData, PointOfInterest.class);
        database.listen(DatabaseWrapper.POINT_OF_INTEREST_PATH, listener);
    }

    @Override
    public PointOfInterestAdapter.PointOfInterestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_point_of_interest, parent, false);
        return new PointOfInterestAdapter.PointOfInterestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointOfInterestHolder holder, int position) {
        holder.nameTextView.setText(pointOfInterestData.get(position).getName());
        holder.typeTextView.setText(pointOfInterestData.get(position).getType());
        holder.locationTextView.setText(pointOfInterestData.get(position).getLocation().toString());
    }

    @Override
    public int getItemCount() {
        return pointOfInterestData.size();
    }

    public static class PointOfInterestHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView typeTextView;
        public TextView locationTextView;

        public PointOfInterestHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.poiName);
            typeTextView = view.findViewById(R.id.poiType);
            locationTextView = view.findViewById(R.id.poiLocation);
        }
    }
}
