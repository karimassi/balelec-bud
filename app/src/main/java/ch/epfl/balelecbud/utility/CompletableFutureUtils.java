package ch.epfl.balelecbud.utility;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

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

    public static class MergeBiConsumer<A> implements BiConsumer<List<A>, Throwable> {

        private final RecyclerViewData<A, ?> recyclerViewData;

        public MergeBiConsumer(RecyclerViewData<A, ?> recyclerViewData) {
            this.recyclerViewData = recyclerViewData;
        }

        @Override
        public void accept(List<A> downloadedList, Throwable throwable) {
            List<A> initialList = recyclerViewData.getData();
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
}
