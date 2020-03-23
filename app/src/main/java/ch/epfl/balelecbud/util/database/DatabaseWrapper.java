package ch.epfl.balelecbud.util.database;

import java.util.Map;

import ch.epfl.balelecbud.util.Callback;

public interface DatabaseWrapper {

    String FESITVAL_INFORMATION_PATH = "festivalInfo";
    String TRANSPORT_PATH = "transports";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USERS = "users";
    String FRIEND_REQUESTS = "friendRequests";
    String FRIENDSHIPS = "friendships";

    void unregisterListener(DatabaseListener listener);

    void listen(String collectionName, DatabaseListener listener);

    <T> void getDocument(String collectionName, String documentID, Class type, Callback<T> callback);

//    <T> void getDocumentWithFieldCondition(String collectionName, String fieldName, String fieldValue, Class type, Callback<T> callback);

    void updateDocument(String collectionName, String documentID, Map<String,Object> updates, Callback callback);

    <T> void storeDocument(String collectionName, T document, Callback callback);

    <T> void storeDocumentWithID(String collectionName, String documentID, T document, Callback callback);

    void deleteDocument(String collectionName, String documentID, Callback callback);

}
