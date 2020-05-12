package ch.epfl.balelecbud.utility.cache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.utility.database.query.MyQuery;

public interface Cache {

    boolean contains(MyQuery query);

    <T> CompletableFuture<List<T>> get(MyQuery query, final Class<T> tClass) throws IOException;

    CompletableFuture<List<Map<String, Object>>> get(MyQuery query)  throws IOException;

    void put(String collectionName, String id, Object document) throws IOException;

    void flush(String collectionName);

    void flush();

}
