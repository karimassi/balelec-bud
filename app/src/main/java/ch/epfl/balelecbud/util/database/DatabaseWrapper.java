package ch.epfl.balelecbud.util.database;

public interface DatabaseWrapper {

    String FESITVAL_INFORMATION_PATH = "festivalInfo";
    String POINT_OF_INTEREST_PATH = "pointsOfInterest";
    String TRANSPORT_PATH = "transports";
    String CONCERT_SLOTS_PATH = "concertSlots";

    void unregisterListener(DatabaseListener listener);

    void listen(String collectionName, DatabaseListener listener);

}
