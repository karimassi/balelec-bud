package ch.epfl.balelecbud.util.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.function.Consumer;

public interface Database {

    String FESTIVAL_INFORMATION_PATH = "festivalInfo";
    String POINT_OF_INTEREST_PATH = "pointsOfInterest";
    String EMERGENCY_INFO_PATH = "emergencyInfo";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USERS_PATH = "users";
    String TOKENS_PATH = "tokens";
    String LOCATIONS_PATH = "locations";
    String FRIENDSHIPS_PATH = "friendships";
    String FRIEND_REQUESTS_PATH = "friendRequests";
    String SENT_REQUESTS_PATH = "sentRequests";
    String EMERGENCY_NUMBER_PATH = "emergencyNumbers";
    String EMERGENCIES_PATH = "emergencies";

    String DOCUMENT_ID_OPERAND = "documentId";

    void unregisterDocumentListener(String collectionName, String documentID);

    <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type);
    
    <T> CompletableFuture<List<T>> queryWithType(MyQuery query, final Class<T> tClass);

    CompletableFuture<List<Map<String, Object>>> query(MyQuery query);

    void updateDocument(String collectionName, String documentID, Map<String,Object> updates);

    <T> void storeDocument(String collectionName, T document);

    <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document);

    void deleteDocumentWithID(String collectionName, String documentID);

}