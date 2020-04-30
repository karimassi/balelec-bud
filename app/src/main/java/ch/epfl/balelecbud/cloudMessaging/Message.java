package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;

public class Message {

    private static final String TAG = Message.class.getSimpleName();

    public static final String MESSAGE_TYPE_GENERAL = getAppContext().getString(R.string.message_type_general);
    public static final String MESSAGE_TYPE_SOCIAL = getAppContext().getString(R.string.message_type_social);
    public static final String TYPE_FRIEND_REQUEST = getAppContext().getString(R.string.type_friend_request);
    public static final String TYPE_ACCEPT_REQUEST = getAppContext().getString(R.string.type_accept_request);
    public static final String DATA_KEY_TITLE = getAppContext().getString(R.string.data_key_title);
    public static final String DATA_KEY_BODY = getAppContext().getString(R.string.data_key_body);
    public static final String DATA_KEY_TYPE = getAppContext().getString(R.string.data_key_type);
    public static final String FRIEND_REQUEST_TITLE = getAppContext().getString(R.string.friend_request_title);
    public static final String FRIEND_REQUEST_BODY = getAppContext().getString(R.string.friend_request_body);
    public static final String ACCEPT_REQUEST_TITLE = getAppContext().getString(R.string.accept_request_title);
    public static final String ACCEPT_REQUEST_BODY = getAppContext().getString(R.string.accept_request_body);

    private String title;
    private String body;
    private String type;

    public Message(String title, String body, String type) {
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public static void sendFriendshipMessage(User user, String toSend, String type) {
        Log.d(TAG, "Sending friendship message, type: " + type);
        BalelecbudApplication.getAppDatabaseWrapper()
                .getDocument(FirestoreDatabaseWrapper.TOKENS_PATH, user.getUid())
                .whenCompleteAsync((t, throwable) -> {
                    if( t != null ) {
                        if(type.equals(TYPE_FRIEND_REQUEST)) {
                            new Message(FRIEND_REQUEST_TITLE, user.getDisplayName() +
                                    FRIEND_REQUEST_BODY, MESSAGE_TYPE_SOCIAL).sendMessage(toSend);
                        }
                        else if(type.equals(TYPE_ACCEPT_REQUEST)) {
                            new Message(ACCEPT_REQUEST_TITLE, user.getDisplayName() +
                                    ACCEPT_REQUEST_BODY, MESSAGE_TYPE_SOCIAL).sendMessage(toSend);
                        }
                    }
                    else Log.d(TAG,
                            "Didn't find token for this user, stopped sending the message");
                });
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
                            message.put(DATA_KEY_TITLE, title)
                                    .put(DATA_KEY_BODY, body)
                                    .put(DATA_KEY_TYPE, type);
                            send.put("data", message).put("to", token);
                            BalelecbudApplication.getMessagingService().sendMessage(send);
                        } catch (JSONException e) {
                            Log.d(TAG, "Couldn't put data in JSONObj");
                        }
                    }
                    else Log.d(TAG,
                            "Didn't find token for this user, stopped sending the message");
                });
    }

    public static Map<String, String> extractMessage(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Received Message");
            return createMessage(remoteMessage.getData().get(DATA_KEY_TITLE),
                    remoteMessage.getData().get(DATA_KEY_BODY),
                    remoteMessage.getData().get(DATA_KEY_TYPE));
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Received Notification");
            return createMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), MESSAGE_TYPE_GENERAL);
        }
        return new HashMap<>();
    }

    public static Map<String, String> createMessage(String title, String body, String type) {
        Map<String, String> message = new HashMap<>();
        message.put(DATA_KEY_TITLE, title);
        message.put(DATA_KEY_BODY, body);
        message.put(DATA_KEY_TYPE, type);
        return message;
    }
}