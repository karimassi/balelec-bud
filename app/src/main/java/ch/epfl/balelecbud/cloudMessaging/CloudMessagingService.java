package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.notifications.NotificationMessage;

public class CloudMessagingService extends FirebaseMessagingService implements MessagingService {

    private static final String TAG = CloudMessagingService.class.getSimpleName();
    private static final CloudMessagingService instance = new CloudMessagingService();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "The token refreshed: " + s);

        Message.setToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "In Cloud Messaging Service");

        receiveMessage(remoteMessage);
    }

    @Override
    public void sendMessage(JSONObject send) {
        Log.d(TAG, "Sending the message to the server");
        BalelecbudApplication.getHttpClient().post(Message.BASE_URL, send);
    }

    @Override
    public void receiveMessage(RemoteMessage remoteMessage) {
        Map<String, String> message = new HashMap<>();

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Received Message");
            message.put(Message.DATA_KEY_TITLE, remoteMessage.getData().get(Message.DATA_KEY_TITLE));
            message.put(Message.DATA_KEY_BODY, remoteMessage.getData().get(Message.DATA_KEY_BODY));
            message.put(Message.DATA_KEY_TYPE, remoteMessage.getData().get(Message.DATA_KEY_TYPE));
        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Received Notification");
            message.put(Message.DATA_KEY_TITLE, remoteMessage.getNotification().getTitle());
            message.put(Message.DATA_KEY_BODY, remoteMessage.getNotification().getBody());
            message.put(Message.DATA_KEY_TYPE, Message.MESSAGE_TYPE_GENERAL);
        } else return;

        Log.d(TAG, "About to send notification with title: " + message.get(Message.DATA_KEY_TITLE));

        NotificationMessage.getInstance().scheduleNotification(this, message);
    }

    public static CloudMessagingService getInstance() {
        return instance;
    }
}
