package ch.epfl.balelecbud.util.database;

import android.telecom.Call;

import java.util.List;

import ch.epfl.balelecbud.util.Callback;

public interface DatabaseWrapper {

    String FESITVAL_INFORMATION_PATH = "festivalInfo";
    String TRANSPORT_PATH = "transports";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USERS = "users";

    void unregisterListener(DatabaseListener listener);

    void listen(String collectionName, DatabaseListener listener);

    <T> void getDocument(String collectionName, String documentID, Class type, Callback<T> callback);

    <T> void storeDocument(String collectionName, T document, Callback<T> callback);

    <T> void storeDocumentWithID(String collectionName, String documentID, T document, Callback<T> callback);

}
