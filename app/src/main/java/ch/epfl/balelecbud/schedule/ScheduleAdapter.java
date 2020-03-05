package ch.epfl.balelecbud.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ch.epfl.balelecbud.R;

public class ScheduleAdapter extends RecyclerView.Adapter {
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeSlotView;
        public TextView artistNameView;
        public TextView sceneNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            timeSlotView = itemView.findViewById(R.id.time_slot);
            artistNameView = itemView.findViewById(R.id.artist_name);
            sceneNameView = itemView.findViewById(R.id.scene_name);
        }
    }

    // Store a member variable for the contacts
    private List<Slot> schedule;

    // Pass in the contact array into the constructor
    public ScheduleAdapter(List<Slot> schedule) {
        this.schedule = schedule;
    }

    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View scheduleView = inflater.inflate(R.layout.item_schedule, parent, false);

        ViewHolder viewHolder = new ViewHolder(scheduleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    public void onBindViewHolder(ScheduleAdapter.ViewHolder viewHolder, int position) {
        Slot slot = schedule.get(position);

        viewHolder.timeSlotView.setText(slot.getTimeSlot());
        viewHolder.artistNameView.setText(slot.getArtistName());
        viewHolder.sceneNameView.setText(slot.getSceneName());
    }

    public int getItemCount() {
        return schedule.size();
    }
}
