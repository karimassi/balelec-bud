package ch.epfl.balelecbud.util.http;

import com.google.gson.JsonElement;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {

    CompletableFuture<JsonElement> get(String url);
}
