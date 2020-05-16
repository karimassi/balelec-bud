package ch.epfl.balelecbud.utility.http;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

final class HttpPostRequest extends CompletableFuture<JsonElement> {

    private static final String TAG = HttpPostRequest.class.getSimpleName();

    private JsonObjectRequest postRequest;

    HttpPostRequest(String url, JSONObject request, String key) {
        Log.d(TAG, "In http post request");
        postRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                response -> complete(JsonParser.parseString(response.toString())),
                error -> {
                    Log.d(TAG, error.toString());
                    completeExceptionally(error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", key);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
    }

    JsonObjectRequest getPostRequest() {
        return postRequest;
    }
}
