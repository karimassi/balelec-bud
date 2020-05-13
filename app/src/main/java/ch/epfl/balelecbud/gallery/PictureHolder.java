package ch.epfl.balelecbud.gallery;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class PictureHolder extends RecyclerView.ViewHolder {
    final ImageView imageView;

    public PictureHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.galleryImageView);
    }
}
