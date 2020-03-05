package ch.epfl.balelecbud.schedule;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

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