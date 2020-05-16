package ch.epfl.balelecbud.utility.http;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

/**
 * Interface modeling an HTTP client
 */
public interface HttpClient {

    /**
     * Perform an HTTP get request
     *
     * @param url the url to get
     * @return    a {@code CompletableFuture} that will complete with the result of the request
     */
    CompletableFuture<JsonElement> get(String url);

    /**
     * Perform an HTTP post request
     *
     * @param url     the url to post request
     * @param request the request
     * @return        a {@code CompletableFuture} that will complete with the result of the request
     */
    CompletableFuture<JsonElement> post(String url, JSONObject request);
}
