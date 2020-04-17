package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.atomic.AtomicInteger;

public class Message {

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

    public void sendMessage(String token) {
        Log.d("CloudMessagingService", "In send message, token: " + token);

        //TODO: verify SENDER_ID
        String SENDER_ID = "138520216656";

        AtomicInteger msgId = new AtomicInteger();

        //TODO: notification id -- send messages to specific groups
        RemoteMessage remoteMessage = new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(msgId.toString())
                .addData(DATA_KEY_TITLE, title)
                .addData(DATA_KEY_BODY, body)
                .setMessageType(type)
                .build();

        FirebaseMessaging.getInstance().send(remoteMessage);
    }
}
