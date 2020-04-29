package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class Message {

    private static final String TAG = Message.class.getSimpleName();

    public static final String MESSAGE_TYPE_GENERAL = "GENERAL";
    public static final String MESSAGE_TYPE_SOCIAL = "SOCIAL";
    public static final String DATA_KEY_TITLE = "title";
    public static final String DATA_KEY_BODY = "body";
    public static final String DATA_KEY_TYPE = "type";
    public static final String FRIEND_REQUEST_TITLE = "You received a friend request!";
    public static final String FRIEND_REQUEST_BODY = " sent you a friend request";
    public static final String ACCEPT_REQUEST_TITLE = "You have a new friend!";
    public static final String ACCEPT_REQUEST_BODY = " accepted your friend request";

    private String title;
    private String body;
    private String type;

    public Message(String title, String body, String type) {
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public static void sendFriendRequestMessage(User sender, String uid) {
        Log.d(TAG, "In send friend request message to uid: " + uid);
        BalelecbudApplication.getAppDatabaseWrapper()
                .getDocument(FirestoreDatabaseWrapper.TOKENS_PATH, sender.getUid())
                .whenCompleteAsync((t, throwable) -> {
                    if( t != null ) {
                        new Message(FRIEND_REQUEST_TITLE, sender.getDisplayName() +
                                FRIEND_REQUEST_BODY, MESSAGE_TYPE_SOCIAL).sendMessage(uid);
                    }
                    else Log.d(TAG,
                            "Didn't find token for this user, stopped sending the message");
                });
    }

    public static void sendAcceptRequestMessage(User friend, String uid) {
        Log.d(TAG, "In send accept request message to uid: " + uid);
        BalelecbudApplication.getAppDatabaseWrapper()
                .getDocument(FirestoreDatabaseWrapper.TOKENS_PATH, friend.getUid())
                .whenCompleteAsync((t, throwable) -> {
                    if( t != null ) {
                        new Message(ACCEPT_REQUEST_TITLE, friend.getDisplayName() +
                                ACCEPT_REQUEST_BODY, MESSAGE_TYPE_SOCIAL).sendMessage(uid);
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
            return createMessage(remoteMessage.getData().get(Message.DATA_KEY_TITLE),
                    remoteMessage.getData().get(Message.DATA_KEY_BODY),
                    remoteMessage.getData().get(Message.DATA_KEY_TYPE));
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Received Notification");
            return createMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), Message.MESSAGE_TYPE_GENERAL);
        }
        return new HashMap<>();
    }

    public static Map<String, String> createMessage(String title, String body, String type) {
        Map<String, String> message = new HashMap<>();
        message.put(Message.DATA_KEY_TITLE, title);
        message.put(Message.DATA_KEY_BODY, body);
        message.put(Message.DATA_KEY_TYPE, type);
        return message;
    }
}