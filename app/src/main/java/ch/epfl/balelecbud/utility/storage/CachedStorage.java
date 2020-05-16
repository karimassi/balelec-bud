package ch.epfl.balelecbud.utility.storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Uses the decorator pattern, may seem slightly redundant now since the inner Storage already
 * returns a file but if we want to implement a caching strategy (like LRU for example) then having
 * this infrastructure already in place will be really useful
 */
public class CachedStorage implements Storage {

    private static final String TAG = CachedStorage.class.getSimpleName();
    private final Storage inner;
    private final Cache cache;

    public CachedStorage(Storage inner, Cache cache) {
        this.inner = inner;
        this.cache = cache;
    }

    @Override
    public CompletableFuture<File> getFile(String name) {
        if (cache.contains(name)) {
            return cache.get(name);
        } else {
            return inner.getFile(name)
                    .thenApply(innerFile -> {
                        try {
                            return cache.put(innerFile, name);
                        } catch (IOException e) {
                            return innerFile;
                        }
                    });
        }
    }

}
