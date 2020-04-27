package ch.epfl.balelecbud.util.http;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HttpPostRequest extends CompletableFuture<JsonElement> {

    private static final String TAG = HttpPostRequest.class.getSimpleName();
    private static final String key = "key=AAAAIEByxFA:APA91bHhnxIzhsfli52m8kq9uP9VWvIB972DTJYz85_ndFCzeDEzEDdgiYVjrVo8yM9npWNH5VchrfNqWw--1-SXB35YS7HIX04_-_9FmiUdJAlYzrRnN2B9q__7t9hXWsIC_rkzgRiv";

    private JsonObjectRequest postRequest;

    public HttpPostRequest(String url, JSONObject request) {
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

    public JsonObjectRequest getPostRequest() {
        return postRequest;
    }

}
