package ch.epfl.balelecbud.cloudMessaging;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.notifications.NotificationMessage;

public class CloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = CloudMessagingService.class.getSimpleName();

    private Context context = this;

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
        }
        else return;

        Log.d(TAG, "About to send notification with title: " + message.get(Message.DATA_KEY_TITLE));

        NotificationMessage.getInstance().scheduleNotification(context, message);
    }

    @VisibleForTesting
    protected void setContext(Context context) {
        this.context = context;
    }
}
