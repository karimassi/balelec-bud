package ch.epfl.balelecbud.util.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.BalelecbudApplication;


public class CachedDatabase implements Database {

    private static final CachedDatabase instance = new CachedDatabase(BalelecbudApplication.getAppDatabase());
    Database database;

    public static CachedDatabase getInstance() {
        return instance;
    }

    private CachedDatabase(Database database) {
        this.database = database;
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
        if (Cache.isCached(query)) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        } else {
            return database.queryWithType(query, tClass);
        }
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> query(MyQuery query) {
        if (Cache.isCached(query)) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        } else {
            return database.query(query);
        }
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
        database.updateDocument(collectionName, documentID, updates);
    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
        storeDocument(collectionName, document);
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        return database.storeDocumentWithID(collectionName, documentID, document);
    }

    @Override
    public void deleteDocumentWithID(String collectionName, String documentID) {
        database.deleteDocumentWithID(collectionName, documentID);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) BalelecbudApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
