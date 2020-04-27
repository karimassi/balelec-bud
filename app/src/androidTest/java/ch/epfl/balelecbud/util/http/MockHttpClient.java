package ch.epfl.balelecbud.util.http;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.cloudMessaging.CloudMessagingService;
import ch.epfl.balelecbud.cloudMessaging.CloudMessagingServiceTest;
import ch.epfl.balelecbud.cloudMessaging.Message;
import ch.epfl.balelecbud.util.CompletableFutureUtils;

public class MockHttpClient implements HttpClient {

    private static final HttpClient instance = new MockHttpClient();

    JsonElement responseStations;

    private MockHttpClient() {
        responseStations = new JsonObject();
        setupStations();
        setupConnections();
    }

    @Override
    public CompletableFuture<JsonElement> get(String url) {
        URL u;
        try {
            u = new URL(url);
            return CompletableFuture.completedFuture(responseStations.getAsJsonObject().get(u.getPath()));
        } catch (MalformedURLException e) {
            Log.d(this.getClass().getSimpleName(), "Could not parse URL");
        }
        return CompletableFutureUtils.getExceptionalFuture("Received an invalid URL");
    }

    @Override
    public CompletableFuture<JsonElement> post(String url, JSONObject request) {
        try {
            setupMessage(url, request);
            return VolleyHttpClient.getInstance().post(url, request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return CompletableFutureUtils.getExceptionalFuture("Received an invalid URL");
    }

    public static HttpClient getInstance() {
        return instance;
    }

    private void setupMessage(String url, JSONObject request) throws JSONException {
        if(url.equals(Message.BASE_URL)) {
            Map<String, String> message = new HashMap<>();
            JSONObject data = (JSONObject) request.get("data");
            message.put(Message.DATA_KEY_TITLE, (String) data.get(Message.DATA_KEY_TITLE));
            message.put(Message.DATA_KEY_BODY, (String) data.get(Message.DATA_KEY_BODY));
            RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message)
                    .setMessageType((String) data.get(Message.DATA_KEY_TYPE)).build();
            CloudMessagingServiceTest.cloudMessagingService.onMessageReceived(rm);
        }
    }

    private void setupStations() {
        JsonArray stations = new JsonArray();

        JsonObject item = new JsonObject();
        item.addProperty("id", "0");
        item.addProperty("name", "Lausanne");
        item.addProperty("distance", 100);
        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", 46.520378);
        coordinates.addProperty("y", 6.568446);
        item.add("coordinate", coordinates);
        stations.add(item);

        item = new JsonObject();
        item.add("id", null);
        stations.add(item);

        JsonObject result = new JsonObject();
        result.add("stations", stations);
        responseStations.getAsJsonObject().add("/v1/locations", result);
    }

    private void setupConnections() {
        JsonArray stationBoard = new JsonArray();

        JsonObject station = new JsonObject();
        station.addProperty("id", "0");

        JsonObject connection = new JsonObject();
        JsonObject stop = new JsonObject();
        stop.addProperty("departureTimestamp", 1585936200);
        connection.add("stop", stop);
        connection.addProperty("category", "M");
        connection.addProperty("number", "m1");
        connection.addProperty("to", "Renens Gare");

        stationBoard.add(connection);

        JsonObject result = new JsonObject();
        result.add("station", station);
        result.add("stationboard", stationBoard);
        responseStations.getAsJsonObject().add("/v1/stationboard", result);
    }
}
