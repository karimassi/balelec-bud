package ch.epfl.balelecbud.utility.storage;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.utility.InformationSource;

/**
 * Uses the decorator pattern, may seem slightly redundant now since the inner Storage already
 * returns a file but if we want to implement a caching strategy (like LRU for example) then having
 * this infrastructure already in place will be really useful
 */
public final class CachedStorage implements Storage {

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

    @Override
    public CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source) {
        Log.d(TAG, "getAllFileNameIn " + collectionName);
        switch(source){
            case REMOTE_ONLY:
            case CACHE_FIRST:
                return inner.getAllFileNameIn(collectionName, source);
            case CACHE_ONLY:
            default:
                CompletableFuture<List<String>> res = new CompletableFuture<>();
                res.complete(new LinkedList<>());
                return res;
        }
    }

}
