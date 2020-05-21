package ch.epfl.balelecbud.view.gallery;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class PictureHolder extends RecyclerView.ViewHolder {
    final ImageView imageView;

    PictureHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.galleryImageView);
    }
}
