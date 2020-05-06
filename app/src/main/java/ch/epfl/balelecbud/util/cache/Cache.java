package ch.epfl.balelecbud.util.cache;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.util.database.MyQuery;

public interface Cache {

    boolean contains(MyQuery query);

    boolean contains(String collectionName, Object o);

    <T> CompletableFuture<List<T>> get(MyQuery query, final Class<T> tClass) throws FileNotFoundException;

    CompletableFuture<List<Map<String, Object>>> get(MyQuery query)  throws FileNotFoundException;

    <T> void put(String collectionName, String id, T document) throws IOException;

    void put(String collectionName, String id, Map<String, Object> document) throws IOException;

    void flush();

}
