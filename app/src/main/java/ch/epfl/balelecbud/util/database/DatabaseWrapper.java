package ch.epfl.balelecbud.util.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DatabaseWrapper {

    String FESITVAL_INFORMATION_PATH = "festivalInfo";
    String TRANSPORT_PATH = "transports";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USER_PATH = "users";
    String LOCATIONS_PATH = "locations";

    void unregisterListener(DatabaseListener listener);

    void listen(String collectionName, DatabaseListener listener);

    <T> CompletableFuture<List<T>> query(MyQuery query, final Class<T> tClass);
}
