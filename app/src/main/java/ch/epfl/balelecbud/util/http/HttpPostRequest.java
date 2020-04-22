package ch.epfl.balelecbud.util.http;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class HttpPostRequest extends CompletableFuture<JsonElement> {

    private static final String TAG = HttpGetRequest.class.getSimpleName();

    private JsonObjectRequest getRequest;

    public HttpPostRequest(String url, JSONObject request) {
        getRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                response -> {
                    complete(JsonParser.parseString(response.toString()));
                },
                error -> {
                    Log.d(TAG, error.toString());
                    completeExceptionally(error);
                }
        );
    }

    public JsonObjectRequest getGetRequest() {
        return getRequest;
    }

}
