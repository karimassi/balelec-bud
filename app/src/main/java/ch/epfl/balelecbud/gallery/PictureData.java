package ch.epfl.balelecbud.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;

public class PictureData extends RecyclerViewData<Picture, PictureHolder> {
    private static final String TAG = PictureData.class.getSimpleName();

    public PictureData() { }

    @Override
    public void reload(Database.Source preferredSource) {
        getAppStorage()
    }

    @Override
    public void bind(int index, PictureHolder viewHolder) {
        final Picture picture = data.get(index);

        CompletableFuture<File> imageDownload = getAppStorage().getFile("users_pictures/" + picture.getImageFileName());
        imageDownload.whenComplete((file, t) -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            viewHolder.imageView.setImageBitmap(bitmap);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        });
    }
}
