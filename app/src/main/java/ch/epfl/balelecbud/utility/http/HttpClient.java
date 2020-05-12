package ch.epfl.balelecbud.utility.http;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {

    CompletableFuture<JsonElement> get(String url);

    CompletableFuture<JsonElement> post(String url, JSONObject request);
}
