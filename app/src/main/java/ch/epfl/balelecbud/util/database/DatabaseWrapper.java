package ch.epfl.balelecbud.util.database;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseWrapper {

    String FESITVAL_INFORMATION_PATH = "festivalInfo";
    String POINT_OF_INTEREST_PATH = "pointsOfInterest";
    String TRANSPORT_PATH = "transports";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USERS = "users";
    String FRIEND_REQUESTS = "friendRequests";
    String FRIENDSHIPS = "friendships";

    void unregisterListener(DatabaseListener listener);

    void listen(String collectionName, DatabaseListener listener);

    <T> CompletableFuture<T> getDocument(String collectionName, String documentID, Class<T> type);

//    <T> void getDocumentWithFieldCondition(String collectionName, String fieldName, String fieldValue, Class type, Callback<T> callback);

    void updateDocument(String collectionName, String documentID, Map<String,Object> updates);

    <T> void storeDocument(String collectionName, T document);

    <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document);

    void deleteDocument(String collectionName, String documentID);

}
