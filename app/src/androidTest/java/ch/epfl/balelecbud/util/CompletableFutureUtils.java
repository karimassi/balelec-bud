package ch.epfl.balelecbud.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class CompletableFutureUtils {

    public static <T> CompletableFuture<T> getExceptionalFuture(final String message) {
        return CompletableFuture.completedFuture(null).thenApply(new Function<Object, T>() {
            @Override
            public T apply(Object o) {
                throw new RuntimeException(message);
            }
        });
    }


}
