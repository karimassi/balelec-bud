package ch.epfl.balelecbud.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {

    private List<Slot> slots;

    public ScheduleAdapter() {
        slots = new ArrayList<>();
        ScheduleAdapterFacade facade = new ScheduleAdapterFacade() {
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
        ScheduleProvider.subscribeSlots(new FirebaseScheduleDatabase(), facade, slots);
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View scheduleView = inflater.inflate(R.layout.item_schedule, parent, false);
        ScheduleViewHolder viewHolder = new ScheduleViewHolder(scheduleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder viewHolder, int position) {
        Slot slot = slots.get(position);
        viewHolder.timeSlotView.setText(slot.getTimeSlot());
        viewHolder.artistNameView.setText(slot.getArtistName());
        viewHolder.sceneNameView.setText(slot.getSceneName());
    }

    public int getItemCount() {
        return slots.size();
    }
}
