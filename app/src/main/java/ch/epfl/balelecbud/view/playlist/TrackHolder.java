package ch.epfl.balelecbud.view.playlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class TrackHolder extends RecyclerView.ViewHolder {
    final TextView title;
    final TextView artist;
    final ImageView image;

    public TrackHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.track_title);
        artist = itemView.findViewById(R.id.track_artist);
        image = itemView.findViewById(R.id.track_image);
    }
}