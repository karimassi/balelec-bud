package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.android.gestures.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.util.TokenUtils;
import ch.epfl.balelecbud.util.http.HttpClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

public class Message {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";

    public static final String MESSAGE_TYPE_REQUEST_FRIENDSHIP = "REQUEST_FRIENDSHIP";
    public static final String MESSAGE_TYPE_FRIENDSHIP = "FRIENDSHIP";
    public static final String DATA_KEY_TITLE = "title";
    public static final String DATA_KEY_BODY = "body";
    public static final String DATA_KEY_TYPE = "type";

    private String type;
    private String title;
    private String body;

    public Message(String type, String title, String body) {
        this.type = type;
        this.title = title;
        this.body = body;
    }

    public void sendMessage(String token) throws IOException, JSONException {
        Log.d("CloudMessagingService", "In send message, token: " + token);

        /*
        // USING GETHTTPCLIENT //

        JSONObject send = new JSONObject();
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();

        notification.put("title", "This is the title").put("body", "This is the body");
        message.put("notification", notification);
        send.put("validate_only", false).put("message", message)
            .put("token", token);

        BalelecbudApplication.getHttpClient().post("https://fcm.googleapis.com/v1/{parent=projects/138520216656*}/messages:send", send);

        */


        /*
        // USING OKHHTP //

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAIEByxFA:APA91bHhnxIzhsfli52m8kq9uP9VWvIB972DTJYz85_ndFCzeDEzEDdgiYVjrVo8yM9npWNH5VchrfNqWw--1-SXB35YS7HIX04_-_9FmiUdJAlYzrRnN2B9q__7t9hXWsIC_rkzgRiv")
                .post(RequestBody.create(MediaType.parse("application/json"), send.toString()))
                .build();
        */



        // USING SEND FROM FIREBASE_MESSAGING //

        //String SENDER_ID = "138520216656";
        String SENDER_ID = token;
        AtomicInteger msgId = new AtomicInteger();

        RemoteMessage remoteMessage = new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(msgId.toString())
                .addData("title", "This is the title")
                .setMessageType(type)
                .build();

        FirebaseMessaging.getInstance().send(remoteMessage);

    }
}
