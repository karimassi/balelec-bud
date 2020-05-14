package ch.epfl.balelecbud.utility.http;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;

public class VolleyHttpClient implements HttpClient {

    private static final HttpClient instance = new VolleyHttpClient();

    RequestQueue queue;

    private VolleyHttpClient() {
        queue = Volley.newRequestQueue(BalelecbudApplication.getAppContext());
    }

    @Override
    public CompletableFuture<JsonElement> get(String url) {
        HttpGetRequest request = new HttpGetRequest(url);
        queue.add(request.getGetRequest());
        return request;
    }

    @Override
    public CompletableFuture<JsonElement> post(String url, JSONObject r) {
        HttpPostRequest request = new HttpPostRequest(url, r);
        queue.add(request.getPostRequest());
        return request;
    }

    public static HttpClient getInstance() {
        return instance;
    }
}