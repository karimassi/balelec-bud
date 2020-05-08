package ch.epfl.balelecbud.schedule;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class SlotHolder extends RecyclerView.ViewHolder {
    final TextView timeSlotView;
    final TextView artistNameView;
    final TextView sceneNameView;
    final ImageView artistImageView;
    final Switch subscribeSwitch;

    public SlotHolder(View itemView) {
        super(itemView);

        timeSlotView = itemView.findViewById(R.id.ScheduleTimeSlot);
        artistNameView = itemView.findViewById(R.id.ScheduleArtistName);
        sceneNameView = itemView.findViewById(R.id.ScheduleSceneName);
        subscribeSwitch = itemView.findViewById(R.id.ScheduleSubscribeSwitch);
        artistImageView = itemView.findViewById(R.id.ScheduleArtistImage);
    }
}
