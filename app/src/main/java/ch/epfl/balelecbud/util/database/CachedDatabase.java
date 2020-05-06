package ch.epfl.balelecbud.util.database;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.util.cache.Cache;
import ch.epfl.balelecbud.util.cache.FileSystemCache;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public class CachedDatabase implements Database {

    private static final String TAG = CachedDatabase.class.getSimpleName();

    private Database database;
    private Cache cache;
    private static CachedDatabase instance = new CachedDatabase(FirestoreDatabase.getInstance(), new FileSystemCache());

    private CachedDatabase(Database database, Cache cache) {
        this.database = database;
        this.cache = cache;
    }

    public static CachedDatabase getInstance() {
        return instance;
    }

    @Override
    public void unregisterDocumentListener(String collectionName, String documentID) {
        database.unregisterDocumentListener(collectionName, documentID);
    }

    @Override
    public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) {
        database.listenDocument(collectionName, documentID, consumer, type);
    }

    @Override
    public <T> CompletableFuture<List<T>> queryWithType(MyQuery query, Class<T> tClass) {
        CompletableFuture<List<T>> result = CompletableFuture.completedFuture(new ArrayList<>());
        if (cache.contains(query)) {
            try {
                result = cache.get(query, tClass);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "queryWithType: " + e.getLocalizedMessage());
            }
        } else {
            result = database.queryWithType(query, tClass);
            result.whenComplete((ts, throwable) -> {
                if (throwable == null) {
                    for (T t : ts) {
                        String id = query.hasDocumentIdOperand() ? query.getIdOperand() : String.valueOf(t.hashCode());
                        try {
                            cache.put(query.getCollectionName(), id, t);
                        } catch (IOException e) {
                            Log.d(TAG, "queryWithType: " + e.getLocalizedMessage());
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
        if (cache.contains(query)) {
            try {
                result = cache.get(query);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "query: " + e.getLocalizedMessage());
            }
        } else {
            result = database.query(query);
            result.whenComplete((maps, throwable) -> {
               if (throwable == null) {
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
        database.updateDocument(collectionName, documentID, updates);
    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
        database.storeDocument(collectionName, document);
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        return database.storeDocumentWithID(collectionName, documentID, document);
    }

    @Override
    public void deleteDocumentWithID(String collectionName, String documentID) {
        database.deleteDocumentWithID(collectionName, documentID);
    }
}
