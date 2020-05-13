package ch.epfl.balelecbud.gallery;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public final class CompletableFutureGallery {

    public static <T> CompletableFuture<T> getExceptionalFuture(final String message) {
        return CompletableFuture.completedFuture(null).thenApply(o -> {
            throw new RuntimeException(message);
        });
    }

    public static <T> CompletableFuture<List<T>> unify(final List<CompletableFuture<T>> listOfFuture) {
        return CompletableFuture.allOf(listOfFuture.toArray(new CompletableFuture[0])).thenApply(aVoid -> {
            List<T> itemList = new LinkedList<>();
            for (CompletableFuture<T> future : listOfFuture) {
                try {
                    itemList.add(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    //cannot happen because we know that all futures have completed normally,
                    // otherwise thenApply could not be called none of the gets are blocking
                    // since we call allOf
                    e.printStackTrace();
                }
            }
            return itemList;
        });
    }

    public static class MergeBiConsumer implements BiConsumer<List<Picture>, Throwable> {

        private final PictureData pictureData;

        public MergeBiConsumer(PictureData pictureData) {
            this.pictureData = pictureData;
        }

        @Override
        public void accept(List<Picture> downloadedList, Throwable throwable) {
            List<Picture> initialList = pictureData.getData();
            for (int i = 0; i < initialList.size(); ++i) {
                if (!downloadedList.contains(initialList.get(i))) {
                    pictureData.remove(i);
                }
            }
            for (int i = 0; i < downloadedList.size(); ++i) {
                Picture downloadedElem = downloadedList.get(i);
                if (!initialList.contains(downloadedElem)) {
                    pictureData.add(pictureData.size(), downloadedElem);
                }
            }
        }
    }
}
