package ch.epfl.balelecbud.schedule;

import android.content.Context;
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
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private static DatabaseWrapper database = new FirestoreDatabaseWrapper();

    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper){
        database = databaseWrapper;
    }

    private List<Slot> slots;

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {
        public TextView timeSlotView;
        public TextView artistNameView;
        public TextView sceneNameView;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            timeSlotView = itemView.findViewById(R.id.time_slot);
            artistNameView = itemView.findViewById(R.id.artist_name);
            sceneNameView = itemView.findViewById(R.id.scene_name);
        }
    }

    public ScheduleAdapter() {
        slots = new ArrayList<>();
        RecyclerViewAdapterFacade facade = new RecyclerViewAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                ScheduleAdapter.this.notifyItemInserted(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                ScheduleAdapter.this.notifyItemChanged(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                ScheduleAdapter.this.notifyItemRemoved(position);
            }
        };
        DatabaseListener<Slot> listener = new DatabaseListener(facade, slots, Slot.class);
        database.listen(DatabaseWrapper.CONCERT_SLOTS_PATH, listener);
    }

    @NonNull
    @Override
    public ScheduleAdapter.ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View scheduleView = inflater.inflate(R.layout.item_schedule, parent, false);
        ScheduleViewHolder viewHolder = new ScheduleViewHolder(scheduleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ScheduleViewHolder viewHolder, int position) {
        Slot slot = slots.get(position);
        viewHolder.timeSlotView.setText(slot.getTimeSlot());
        viewHolder.artistNameView.setText(slot.getArtistName());
        viewHolder.sceneNameView.setText(slot.getSceneName());
    }

    public int getItemCount() {
        return slots.size();
    }
}
