package ch.epfl.balelecbud.utility.http;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.concurrent.CompletableFuture;

final class HttpGetRequest extends CompletableFuture<JsonElement> {

    private static final String TAG = HttpGetRequest.class.getSimpleName();

    private JsonObjectRequest getRequest;

    HttpGetRequest(String url) {
        getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    complete(JsonParser.parseString(response.toString()));
                },
                error -> {
                    Log.d(TAG, error.toString());
                    completeExceptionally(error);
                }
        );
    }

    JsonObjectRequest getGetRequest() {
        return getRequest;
    }
}