package ch.epfl.balelecbud.view.gallery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.Picture;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;
import static ch.epfl.balelecbud.utility.storage.Storage.USER_PICTURES;

public final class PictureData extends RecyclerViewData<Picture, PictureHolder> {
    private static final String TAG = PictureData.class.getSimpleName();

    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        super.clearAll();
        CompletableFuture<List<String>> list = getAppStorage().getAllFileNameIn(USER_PICTURES, preferredSource);
        return list.whenComplete((file, t) ->
                file.forEach(path -> {
                    Picture p = new Picture(path);
                    add(size(), p);
                })
        ).thenApply(x -> null);
    }

    @Override
    public void bind(int index, PictureHolder viewHolder) {
        final Picture picture = data.get(index);
        CompletableFutureUtils.downloadAndSetImageView(picture.getImageFileName(),
                viewHolder.imageView, TAG, "Could not load picture");
    }
}
