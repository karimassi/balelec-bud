package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;

import static ch.epfl.balelecbud.util.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;

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

        MyQuery query = new MyQuery(Database.TOKENS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, uid));
        BalelecbudApplication.getAppDatabase()
                .query(query)
                .whenCompleteAsync((t, throwable) -> {
                    if( t != null ) {
                        String token = new ArrayList<>(t.get(0).keySet()).get(0);
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
                    remoteMessage.getMessageType());
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