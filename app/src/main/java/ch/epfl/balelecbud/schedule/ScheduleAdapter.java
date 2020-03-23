package ch.epfl.balelecbud.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import ch.epfl.balelecbud.notifications.concertFlow.AbstractConcertFlow;
import ch.epfl.balelecbud.notifications.concertFlow.FlowUtil;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    public interface ServiceInterface {
        void callService(@NonNull Intent intent);
    }

    private static final String TAG = ScheduleAdapter.class.getSimpleName();

    private static DatabaseWrapper database = FirestoreDatabaseWrapper.getInstance();

    private ServiceInterface service = new ServiceInterface() {
        @Override
        public void callService(@NonNull Intent intent) {
            ScheduleAdapter.this.mainActivity.startService(intent);
        }
    };

    private final Activity mainActivity;

    @VisibleForTesting
    public void setService(ServiceInterface service) {
        this.service = service;
    }

    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper) {
        database = databaseWrapper;
    }

    private List<Slot> slots;

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        final TextView timeSlotView;
        final TextView artistNameView;
        final TextView sceneNameView;
        final Switch subscribeSwitch;

        ScheduleViewHolder(View itemView) {
            super(itemView);

            timeSlotView = itemView.findViewById(R.id.ScheduleTimeSlot);
            artistNameView = itemView.findViewById(R.id.ScheduleArtistName);
            sceneNameView = itemView.findViewById(R.id.ScheduleSceneName);
            subscribeSwitch = itemView.findViewById(R.id.ScheduleSubscribeSwitch);
        }
    }

    public ScheduleAdapter(Activity activity) {
        this.mainActivity = activity;
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
                    service.callService(
                            FlowUtil.packSubscribeIntentWithSlot(ScheduleAdapter.this.mainActivity, slot));
                } else {
                    Log.d(TAG, "Notification switched: ON");
                    service.callService(
                            FlowUtil.packCancelIntentWithSlot(ScheduleAdapter.this.mainActivity, slot));
                }
            }
        });

       service.callService(FlowUtil.packCallback(mainActivity, new AbstractConcertFlow.FlowCallback() {
            @Override
            public void onResult(List<Slot> slots) {
                if (slots.contains(slot)) {
                    ScheduleAdapter.this.mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.subscribeSwitch.setChecked(true);
                        }
                    });
                }
            }
        }));
    }

    public int getItemCount() {
        return slots.size();
    }
}
