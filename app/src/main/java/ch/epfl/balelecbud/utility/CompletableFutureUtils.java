package ch.epfl.balelecbud.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;

/**
 * Collection of useful methods to work with {@code CompletableFuture}
 */
public final class CompletableFutureUtils {

    /**
     * From a list of {@code CompletableFuture} creates a new one that will complete when all completed
     *
     * @param listOfFuture a list of {@code CompletableFuture}
     * @return             the new {@code CompletableFuture}
     */
    public static <T> CompletableFuture<List<T>> unify(final List<CompletableFuture<T>> listOfFuture){
        return CompletableFuture.allOf(listOfFuture.toArray(new CompletableFuture[0])).thenApply(aVoid -> {
            List<T> itemList = new LinkedList<>();
            for(CompletableFuture<T> future : listOfFuture){
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

    public static class MergeConsumer<A> implements Consumer<FetchedData<A>> {

        private final RecyclerViewData<A, ?> recyclerViewData;

        public MergeConsumer(RecyclerViewData<A, ?> recyclerViewData) {
            this.recyclerViewData = recyclerViewData;
        }

        @Override
        public void accept(FetchedData<A> downloadedData) {
            List<A> initialList = recyclerViewData.getData();
            List<A> downloadedList = downloadedData.getList();
            for(int i = 0; i < initialList.size(); ++i) {
                if (!downloadedList.contains(initialList.get(i))) {
                    recyclerViewData.remove(i);
                }
            }
            for(int i = 0; i < downloadedList.size(); ++i) {
                A downloadedElem = downloadedList.get(i);
                if(!initialList.contains(downloadedElem)){
                    recyclerViewData.add(recyclerViewData.size(), downloadedElem);
                }
            }
        }
    }

    public static class MergeFunction<T> implements Function<FetchedData<T>, Long> {

        private final RecyclerViewData<T, ?> recyclerViewData;

        public MergeFunction(RecyclerViewData<T, ?> recyclerViewData) {
            this.recyclerViewData = recyclerViewData;
        }

        @Override
        public Long apply(FetchedData<T> fetchedData) {
            List<T> initialList = recyclerViewData.getData();
            List<T> downloadedList = fetchedData.getList();
            for(int i = 0; i < initialList.size(); ++i) {
                if (!downloadedList.contains(initialList.get(i))) {
                    recyclerViewData.remove(i);
                }
            }
            for(int i = 0; i < downloadedList.size(); ++i) {
                T downloadedElem = downloadedList.get(i);
                if(!initialList.contains(downloadedElem)){
                    recyclerViewData.add(recyclerViewData.size(), downloadedElem);
                }
            }
            return fetchedData.getFreshness();
        }
    }

    /**
     * Download an image from storage and display it in the given {@code ImageView}
     *
     * @param filepath  the path of the image
     * @param imageView the {@code ImageView} used to display the image
     * @param TAG       the TAG of the calling class
     * @param log       the log message in case of failure
     */
    public static void downloadAndSetImageView(String filepath, ImageView imageView, String TAG, String log) {
        CompletableFuture<File> imageDownload = getAppStorage().getFile(filepath);
        imageDownload.whenComplete((file, t) -> {
            if (t == null) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            } else {
                Log.w(TAG, log, t);
            }
        });
    }
}
