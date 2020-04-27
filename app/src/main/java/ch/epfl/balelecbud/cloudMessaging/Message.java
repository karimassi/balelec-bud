package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class Message {

    private static final String TAG = Message.class.getSimpleName();

    public static final String MESSAGE_TYPE_GENERAL = "GENERAL";
    public static final String DATA_KEY_TITLE = "title";
    public static final String DATA_KEY_BODY = "body";
    public static final String DATA_KEY_TYPE = "type";

    private String title;
    private String body;
    private String type;

    public Message(String title, String body, String type) {
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public void sendMessage(String uid) {
        Log.d(TAG, "In send message, uid: " + uid);

        BalelecbudApplication.getAppDatabaseWrapper()
                .getDocument(FirestoreDatabaseWrapper.TOKENS_PATH, uid)
                .whenCompleteAsync((t, throwable) -> {
                    if( t != null ) {
                        String token = (String) t.get("token");
                        Log.d(TAG, "In send message, token: " + token);

                        JSONObject send = new JSONObject();
                        JSONObject message = new JSONObject();

                        try {
                            message.put(DATA_KEY_TYPE, type)
                                    .put(DATA_KEY_TITLE, title)
                                    .put(DATA_KEY_BODY, body);
                            send.put("data", message).put("to", token);
                            BalelecbudApplication.getMessagingService().sendMessage(send);
                        } catch (JSONException e) {
                            Log.d(TAG, "Couldn't put data in JSONObj");
                            e.printStackTrace();
                        }
                    }
                    else Log.d(TAG,
                            "Didn't find token for this user, stopped sending the message");
                });
    }

    public static Map<String, String> extractMessage(RemoteMessage remoteMessage) {
        Map<String, String> message = new HashMap<>();
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Received Message");
            message.put(Message.DATA_KEY_TITLE, remoteMessage.getData().get(Message.DATA_KEY_TITLE));
            message.put(Message.DATA_KEY_BODY, remoteMessage.getData().get(Message.DATA_KEY_BODY));
            message.put(Message.DATA_KEY_TYPE, remoteMessage.getMessageType());
        }
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Received Notification");
            message.put(Message.DATA_KEY_TITLE, remoteMessage.getNotification().getTitle());
            message.put(Message.DATA_KEY_BODY, remoteMessage.getNotification().getBody());
            message.put(Message.DATA_KEY_TYPE, Message.MESSAGE_TYPE_GENERAL);
        }
        return message;
    }
}