package ch.epfl.balelecbud.utility.cloudMessaging;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.notifications.NotificationMessage;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;

/**
 * Implementation of {@code MessaginService} with Firebase Cloud Messaging
 */
public final class CloudMessagingService extends FirebaseMessagingService implements MessagingService {

    private static final String TAG = CloudMessagingService.class.getSimpleName();
    private static final MessagingService instance = new CloudMessagingService();

    public static MessagingService getInstance() {
        return instance;
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "The token refreshed: " + s);

        TokenUtils.setToken(s);
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
        BalelecbudApplication.getHttpClient().setAuthorizationKey(getAppContext().getString(R.string.fcm_key));
        BalelecbudApplication.getHttpClient().post(getAppContext().getString(R.string.fcm_base_url), send);
    }

    @Override
    public void receiveMessage(RemoteMessage remoteMessage) {
        Map<String, String> message = Message.extractFromMessage(remoteMessage);
        if(message.isEmpty()) {
            return;
        }
        Log.d(TAG, "About to send notification with title: " + message.get(getAppContext().getString(R.string.data_key_title)));
        NotificationMessage.getInstance().scheduleNotification(this, message);
    }
}