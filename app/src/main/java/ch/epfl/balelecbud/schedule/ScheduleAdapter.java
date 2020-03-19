package ch.epfl.balelecbud.schedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.notifications.concertDataBase.ConcertDatabaseInterface;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.adapters.RecyclerViewAdapterFacade;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private static DatabaseWrapper database = new FirestoreDatabaseWrapper();
    private static ConcertDatabaseInterface notifDB;

    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper){
        database = databaseWrapper;
    }

    private List<Slot> slots;

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {
        public TextView timeSlotView;
        public TextView artistNameView;
        public TextView sceneNameView;
        public Switch subscribeSwitch;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            timeSlotView = itemView.findViewById(R.id.time_slot);
            artistNameView = itemView.findViewById(R.id.artist_name);
            sceneNameView = itemView.findViewById(R.id.scene_name);
            subscribeSwitch = itemView.findViewById(R.id.subscribe_switch);
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
        final Slot slot = slots.get(position);
        viewHolder.timeSlotView.setText(slot.getTimeSlot());
        viewHolder.artistNameView.setText(slot.getArtistName());
        viewHolder.sceneNameView.setText(slot.getSceneName());

        /**subscribeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The location is enabled
                    Log.d(TAG, "Location switched: ON");
                    notifDB.scheduleNewConcert(slot);
                } else {
                    // The location is disabled
                    Log.d(TAG,"Location switched: OFF");
                    notifDB.removeConcert(slot);
                }
            }
        });**/
    }

    public int getItemCount() {
        return slots.size();
    }
}
