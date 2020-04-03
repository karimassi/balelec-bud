package ch.epfl.balelecbud.util;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.BiConsumer;

import ch.epfl.balelecbud.util.views.RecyclerViewData;

public final class CompletableFutureUtils {

    public static <T> CompletableFuture<T> getExceptionalFuture(final String message) {
        return CompletableFuture.completedFuture(null).thenApply(new Function<Object, T>() {
            @Override
            public T apply(Object o) {
                throw new RuntimeException(message);
            }
        });
    }

    public static <T> CompletableFuture<List<T>> unify(final List<CompletableFuture<T>> listOfFuture){
        return CompletableFuture.allOf(listOfFuture.toArray(new CompletableFuture[0])).thenApply(new Function<Void, List<T>>() {
            @Override
            public List<T> apply(Void aVoid) {
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
            }
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
