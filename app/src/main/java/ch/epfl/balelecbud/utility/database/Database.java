package ch.epfl.balelecbud.utility.database;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.utility.database.query.MyQuery;

/**
 * Interface modeling a database
 */
public interface Database {

    enum Source {REMOTE, CACHE}

    String FESTIVAL_INFORMATION_PATH = "festivalInfo";
    String POINT_OF_INTEREST_PATH = "pointsOfInterest";
    String EMERGENCY_INFO_PATH = "emergencyInfo";
    String EMERGENCIES_PATH = "emergencies";
    String CONCERT_SLOTS_PATH = "concertSlots";
    String USERS_PATH = "users";
    String TOKENS_PATH = "tokens";
    String LOCATIONS_PATH = "locations";
    String FRIENDSHIPS_PATH = "friendships";
    String FRIEND_REQUESTS_PATH = "friendRequests";
    String SENT_REQUESTS_PATH = "sentRequests";

    String DOCUMENT_ID_OPERAND = "documentId";

    /**
     * Unregister the listener on a given document
     *
     * @param collectionName the collection where the document is located
     * @param documentID     the ID of the document
     */
    void unregisterDocumentListener(String collectionName, String documentID);

    /**
     * Register a listener on a document of the database, the listener will be called each time
     * the document changed with the content of the document
     *
     * @param collectionName the collection where the document is located
     * @param documentID     the ID of the document
     * @param listener       the listener to call
     * @param type           the class of the object contained in the document
     */
    <T> void listenDocument(String collectionName, String documentID, Consumer<T> listener, Class<T> type);

    /**
     * Query the database and cast the results
     *
     * @param query  a query
     * @param tClass the class of the objects to query
     * @return       a {@code CompletableFuture} that will complete with the result of the query
     */
    <T> CompletableFuture<List<T>> query(MyQuery query, final Class<T> tClass);

    /**
     * Query the database
     *
     * @param query a query
     * @return      a {@code CompletableFuture} that will complete with the result of the query
     */
    CompletableFuture<List<Map<String, Object>>> query(MyQuery query);

    /**
     * Update a document of the database
     *
     * @param collectionName the collection where the document is located
     * @param documentID     the ID of the document
     * @param updates        a map of the updates to apply to the document
     */
    void updateDocument(String collectionName, String documentID, Map<String,Object> updates);

    /**
     * Store an object in the database. A new document ID will automatically be created
     *
     * @param collectionName the collection where to store the object
     * @param document       the object to store
     */
    <T> void storeDocument(String collectionName, T document);

    /**
     * Store an object in the database in a given document. It the document already exists it will
     * be overridden
     *
     * @param collectionName the collection where to store the object
     * @param documentID     the ID of the document
     * @param document       the object to store
     * @return               a {@code CompletableFuture} that will complete when the operation finished
     */
    <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document);

    /**
     * Delete a document from the database
     *
     * @param collectionName the collection where the document is stored
     * @param documentID     the ID of the document to delete
     */
    void deleteDocumentWithID(String collectionName, String documentID);

}

