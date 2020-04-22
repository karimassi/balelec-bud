package ch.epfl.balelecbud.util.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.function.Consumer;

public interface DatabaseWrapper {

    String FESTIVAL_INFORMATION_PATH = "festivalInfo";
    String POINT_OF_INTEREST_PATH = "pointsOfInterest";
    String TRANSPORT_PATH = "transports";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USERS_PATH = "users";
    String LOCATIONS_PATH = "locations";
    String FRIENDSHIPS_PATH = "friendships";
    String FRIEND_REQUESTS_PATH = "friendRequests";
    String EMERGENCIES_PATH = "emergencies";

    void unregisterDocumentListener(String collectionName, String documentID);

    <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type);
    
    <T> CompletableFuture<List<T>> query(MyQuery query, final Class<T> tClass);

    CompletableFuture<List<String>> queryIds(MyQuery query);
    
    <T> CompletableFuture<T> getCustomDocument(String collectionName, String documentID, Class<T> type);

    CompletableFuture<Map<String, Object>> getDocument(String collectionName, String documentID);

    <T> CompletableFuture<T> getDocumentWithFieldCondition(String collectionName, String fieldName, String fieldValue, Class<T> type);

    void updateDocument(String collectionName, String documentID, Map<String,Object> updates);

    <T> void storeDocument(String collectionName, T document);

    <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document);

    void deleteDocumentWithID(String collectionName, String documentID);

    void unregisterListeners();
}
