package ch.epfl.balelecbud.util.database;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FileSystemDatabase implements Database {

    @Override
    public void unregisterDocumentListener(String collectionName, String documentID) {

    }

    @Override
    public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) {

    }

    @Override
    public <T> CompletableFuture<List<T>> queryWithType(MyQuery query, Class<T> tClass) {
        return null;
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> query(MyQuery query) {
        return null;
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {

    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {

    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        return null;
    }

    @Override
    public void deleteDocumentWithID(String collectionName, String documentID) {

    }
}
