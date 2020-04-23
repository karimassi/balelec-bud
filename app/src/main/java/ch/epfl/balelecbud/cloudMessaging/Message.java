package ch.epfl.balelecbud.cloudMessaging;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.util.TokenUtils;
import ch.epfl.balelecbud.util.http.HttpClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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

        /*JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();

        notification.put("title", "This is the title").put("body", "This is the body");
        message.put("notification", notification);

        StringRequest myReq = new StringRequest(Request.Method.POST,"https://fcm.googleapis.com/fcm/send",
                response -> Toast.makeText(activity, "Bingo Success", Toast.LENGTH_SHORT).show(),
                error -> {
                    Toast.makeText(activity, "Oops error", Toast.LENGTH_SHORT).show();
                    Log.d("CloudMessagingService", error.toString());
                })
        {
                    @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                Map<String,String> rawParameters = new Hashtable<String, String>();
                rawParameters.put("data", message.toString());
                rawParameters.put("to", token);
                return new JSONObject(rawParameters).toString().getBytes();
            };

            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key="+"AAAAIEByxFA:APA91bHhnxIzhsfli52m8kq9uP9VWvIB972DTJYz85_ndFCzeDEzEDdgiYVjrVo8yM9npWNH5VchrfNqWw--1-SXB35YS7HIX04_-_9FmiUdJAlYzrRnN2B9q__7t9hXWsIC_rkzgRiv");
                return headers;
            }

        };

        Volley.newRequestQueue(activity).add(myReq);*/



        /*
        // USING GETHTTPCLIENT //

        JSONObject send = new JSONObject();
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();

        notification.put("title", "This is the title").put("body", "This is the body");
        message.put("notification", notification);
        send.put("message", message)
            .put("token", token);

        BalelecbudApplication.getHttpClient().post(BASE_URL, send); */


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
                .setMessageType("type")
                .build();

        FirebaseMessaging.getInstance().send(remoteMessage);

    }
}
