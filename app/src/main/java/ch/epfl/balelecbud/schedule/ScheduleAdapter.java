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
import ch.epfl.balelecbud.notifications.concertFlow.ConcertFlowInterface;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private static DatabaseWrapper database = FirestoreDatabaseWrapper.getInstance();

    private static ConcertFlowInterface notifFlow = null;

    @VisibleForTesting
    public static void setConcertFlowInterface(ConcertFlowInterface flow) {
        notifFlow = flow;
    }

    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper) {
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

            timeSlotView = itemView.findViewById(R.id.ScheduleTimeSlot);
            artistNameView = itemView.findViewById(R.id.ScheduleArtistName);
            sceneNameView = itemView.findViewById(R.id.ScheduleSceneName);
            subscribeSwitch = itemView.findViewById(R.id.ScheduleSubscribeSwitch);
        }
    }

    public ScheduleAdapter(ConcertFlowInterface notifFlow) {
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
        DatabaseListener<Slot> listener = new DatabaseListener<>(facade, slots, Slot.class);
        database.listen(DatabaseWrapper.CONCERT_SLOTS_PATH, listener);
        if (ScheduleAdapter.notifFlow == null)
            ScheduleAdapter.notifFlow = notifFlow;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View scheduleView = inflater.inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(scheduleView);
    }

    @Override
    public void onBindViewHolder(final ScheduleAdapter.ScheduleViewHolder viewHolder, int position) {
        final Slot slot = slots.get(position);
        viewHolder.timeSlotView.setText(slot.getTimeSlot());
        viewHolder.artistNameView.setText(slot.getArtistName());
        viewHolder.sceneNameView.setText(slot.getSceneName());

        viewHolder.subscribeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "Notification switched: ON");
                    notifFlow.scheduleNewConcert(slot);
                } else {
                    Log.d(TAG, "Notification switched: ON");
                    notifFlow.removeConcert(slot);
                }
            }
        });
        if (notifFlow.getAllScheduledConcert().contains(slot)) {
            viewHolder.subscribeSwitch.setChecked(true);
        }
    }

    public int getItemCount() {
        return slots.size();
    }
}
