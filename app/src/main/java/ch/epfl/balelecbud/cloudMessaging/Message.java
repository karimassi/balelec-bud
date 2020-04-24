package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class Message {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";

    public static final String MESSAGE_TYPE_FRIENDSHIP = "FRIENDSHIP";
    public static final String MESSAGE_TYPE_FRIEND_REQUEST = "FRIEND_REQUEST";
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

    public void sendMessage(String uid) {
        Log.d("CloudMessagingService", "In send message, uid: " + uid);

        BalelecbudApplication.getAppDatabaseWrapper()
                .getDocument(FirestoreDatabaseWrapper.TOKENS_PATH, uid)
                .whenCompleteAsync((t, throwable) -> {
                    if( t != null ) {
                        String token = (String) t.get("token");
                        Log.d("CloudMessagingService", "In send message, token: " + token);

                        JSONObject send = new JSONObject();
                        JSONObject message = new JSONObject();

                        try {
                            message.put(DATA_KEY_TYPE, type)
                                    .put(DATA_KEY_TITLE, title)
                                    .put(DATA_KEY_BODY, body);
                            send.put("data", message).put("to", token);
                            BalelecbudApplication.getHttpClient().post(BASE_URL, send);
                        } catch (JSONException e) {
                            Log.d("CloudMessagingService", "Couldn't put data in JSONObj");
                            e.printStackTrace();
                        }
                    }
                    else Log.d("CloudMessagingService",
                            "Didn't find token for this user, stopped sending the message");
                });
    }
}