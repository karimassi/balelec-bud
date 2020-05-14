package ch.epfl.balelecbud.utility.cloudMessaging;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;

public class Message {

    private static final String TAG = Message.class.getSimpleName();

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
        MyQuery query = new MyQuery(Database.TOKENS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, user.getUid()));
        BalelecbudApplication.getAppDatabase()
                .query(query)
                .whenCompleteAsync((t, throwable) -> {
                    if( throwable == null && t.size() > 0 ) {
                        if(type.equals(getAppContext().getString(R.string.type_friend_request))) {
                            new Message(getAppContext().getString(R.string.friend_request_title),
                                    user.getDisplayName() + getAppContext().getString(R.string.friend_request_body),
                                    getAppContext().getString(R.string.message_type_social)).sendMessage(toSend);
                        }
                        else if(type.equals(getAppContext().getString(R.string.type_accept_request))) {
                            new Message(getAppContext().getString(R.string.accept_request_title),
                                    user.getDisplayName() + getAppContext().getString(R.string.accept_request_body),
                                    getAppContext().getString(R.string.message_type_social)).sendMessage(toSend);
                        }
                    }
                    else Log.d(TAG,
                            "Didn't find token for this user, stopped sending the message");
                });
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
                            message.put(getAppContext().getString(R.string.data_key_title), title)
                                    .put(getAppContext().getString(R.string.data_key_body), body)
                                    .put(getAppContext().getString(R.string.data_key_type), type);
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
            return createMessage(remoteMessage.getData().get(getAppContext().getString(R.string.data_key_title)),
                    remoteMessage.getData().get(getAppContext().getString(R.string.data_key_body)),
                    remoteMessage.getData().get(getAppContext().getString(R.string.data_key_type)));
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Received Notification");
            return createMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), getAppContext().getString(R.string.message_type_general));
        }
        return new HashMap<>();
    }

    public static Map<String, String> createMessage(String title, String body, String type) {
        Map<String, String> message = new HashMap<>();
        message.put(getAppContext().getString(R.string.data_key_title), title);
        message.put(getAppContext().getString(R.string.data_key_body), body);
        message.put(getAppContext().getString(R.string.data_key_type), type);
        return message;
    }
}