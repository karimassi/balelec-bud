package ch.epfl.balelecbud.utility.cache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

/**
 * Interface modeling a cache
 */
public interface Cache {

    /**
     * Check if the cache contains the result of the query
     * @param query a query
     * @return      {@code true} if the cache contains the result
     */
    boolean contains(MyQuery query);

    /**
     * Query the cache and cast the results
     *
     * @param query  a query
     * @param tClass the resulting class type
     * @return       a {@code CompletableFuture} that will complete with the result of the query
     * @throws IOException if an error occurred
     */
    <T> CompletableFuture<FetchedData<T>> get(MyQuery query, final Class<T> tClass) throws IOException;

    /**
     * Query the cache
     *
     * @param query a query
     * @return      a {@code CompletableFuture} that will complete with the result of the query
     * @throws IOException if an error occurred
     */
    CompletableFuture<FetchedData<Map<String, Object>>> get(MyQuery query) throws IOException;

    /**
     * Add an object to the cache
     *
     * @param collectionName the name of the collection where to store the new object
     * @param id             the id of the object
     * @param document       the object to store
     * @throws IOException   if an error occurred
     */
    void put(String collectionName, String id, Object document) throws IOException;

    /**
     * Flush the given collection from cache
     * @param collectionName the name of the collection to flush
     */
    void flush(String collectionName);

    /**
     * Flush the whole cache
     */
    void flush();

}
