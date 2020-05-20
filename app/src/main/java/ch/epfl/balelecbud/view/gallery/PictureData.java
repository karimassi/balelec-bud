package ch.epfl.balelecbud.view.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.Picture;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;

public final class PictureData extends RecyclerViewData<Picture, PictureHolder> {
    private static final String TAG = PictureData.class.getSimpleName();

    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        super.clearAll();
        /**CompletableFuture<List<String>> names = getAppStorage().getAllFileNameIn("users_pictures");
         names.forEach(name -> {
         Picture p = new Picture(name);
         add(super.size(), p);
         });**/
        CompletableFuture<List<String>> list = getAppStorage().getAllFileNameIn("users_pictures", preferredSource);
        return list.whenComplete((file, t) ->
                file.forEach(path -> {
                    Picture p = new Picture(path);
                    add(size(), p);
                })
        ).thenApply(x -> null);

        /**StorageReference listRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference().child("users_pictures");
         listRef.listAll()
         .addOnSuccessListener(listResult -> {
         for (StorageReference item : listResult.getItems()) {
         Picture p = new Picture(item.getPath().substring(1));
         if(super.size() != listResult.getItems().size())
         super.add(super.size(), p);
         }
         });**/
    }

    @Override
    public void bind(int index, PictureHolder viewHolder) {
        final Picture picture = data.get(index);
        CompletableFuture<File> imageDownload = getAppStorage().getFile(picture.getImageFileName());
        imageDownload.whenComplete((file, t) -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            viewHolder.imageView.setImageBitmap(bitmap);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        });
    }
}
