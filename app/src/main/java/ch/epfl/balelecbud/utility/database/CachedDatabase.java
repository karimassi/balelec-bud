package ch.epfl.balelecbud.utility.database;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.utility.cache.Cache;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppCache;
import static ch.epfl.balelecbud.BalelecbudApplication.getRemoteDatabase;

public class CachedDatabase implements Database {

    private static final String TAG = CachedDatabase.class.getSimpleName();

    private Cache cache;

    private static CachedDatabase instance = new CachedDatabase(getAppCache());

    private CachedDatabase(Cache cache) {
        this.cache = cache;
    }

    public static CachedDatabase getInstance() {
        return instance;
    }

    @Override
    public void unregisterDocumentListener(String collectionName, String documentID) {
        getRemoteDatabase().unregisterDocumentListener(collectionName, documentID);
    }

    @Override
    public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) {
        getRemoteDatabase().listenDocument(collectionName, documentID, consumer, type);
    }

    @Override
    public <T> CompletableFuture<List<T>> query(MyQuery query, Class<T> tClass) {
        CompletableFuture<List<T>> result = CompletableFuture.completedFuture(new ArrayList<>());
        if (query.getSource().equals(Source.CACHE_FIRST) && cache.contains(query)) {
            try {
                result = cache.get(query, tClass);
            } catch (IOException e) {
                Log.d(TAG, "query: " + e.getLocalizedMessage());
            }
        } else {
            result = getRemoteDatabase().query(query, tClass);
            result.whenComplete((ts, throwable) -> {
                if (throwable == null) {
                    cache.flush(query.getCollectionName());
                    for (T t : ts) {
                        String id = query.hasDocumentIdOperand() ? query.getIdOperand() : String.valueOf(t.hashCode());
                        try {
                            cache.put(query.getCollectionName(), id, t);
                        } catch (IOException e) {
                            Log.d(TAG, "query: " + e.getLocalizedMessage());
                        }
                    }
                }
            });
        }
        return result;
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> query(MyQuery query) {
        CompletableFuture<List<Map<String, Object>>> result = CompletableFuture.completedFuture(new ArrayList<>());
        if (query.getSource().equals(Source.CACHE_FIRST) && cache.contains(query)) {
            try {
                result = cache.get(query);
            } catch (IOException e) {
                Log.d(TAG, "query: " + e.getLocalizedMessage());
            }
        } else {
            result = getRemoteDatabase().query(query);
            result.whenComplete((maps, throwable) -> {
               if (throwable == null) {
                   cache.flush(query.getCollectionName());
                   for (Map m : maps) {
                       String id = query.hasDocumentIdOperand() ? query.getIdOperand() : String.valueOf(m.hashCode());
                       try {
                           cache.put(query.getCollectionName(), id, m);
                       } catch (IOException e) {
                           Log.d(TAG, "query: " + e.getLocalizedMessage());
                       }
                   }
               }
            });
        }
        return result;
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
        getRemoteDatabase().updateDocument(collectionName, documentID, updates);
    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
        getRemoteDatabase().storeDocument(collectionName, document);
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        return getRemoteDatabase().storeDocumentWithID(collectionName, documentID, document);
    }

    @Override
    public void deleteDocumentWithID(String collectionName, String documentID) {
        getRemoteDatabase().deleteDocumentWithID(collectionName, documentID);
    }
}
