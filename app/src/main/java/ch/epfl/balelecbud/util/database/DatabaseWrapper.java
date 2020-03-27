package ch.epfl.balelecbud.util.database;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseWrapper {

    String FESTIVAL_INFORMATION_PATH = "festivalInfo";
    String POINT_OF_INTEREST_PATH = "pointsOfInterest";
    String TRANSPORT_PATH = "transports";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USERS_PATH = "users";
    String LOCATIONS_PATH = "locations";
    String FRIENDSHIPS_PATH = "friendships";
    String FRIEND_REQUESTS_PATH = "friendRequests";

    void unregisterListener(DatabaseListener listener);

    void listen(String collectionName, DatabaseListener listener);

    <T> CompletableFuture<T> getCustomDocument(String collectionName, String documentID, Class<T> type);

    CompletableFuture<Map<String, Object>> getDocument(String collectionName, String documentID);

    <T> CompletableFuture<T> getDocumentWithFieldCondition(String collectionName, String fieldName, String fieldValue, Class<T> type);

    void updateDocument(String collectionName, String documentID, Map<String,Object> updates);

    <T> void storeDocument(String collectionName, T document);

    <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document);

    void deleteDocument(String collectionName, String documentID);

}
