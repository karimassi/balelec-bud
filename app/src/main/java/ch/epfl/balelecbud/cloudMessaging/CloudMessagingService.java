package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.notifications.NotificationMessage;
import ch.epfl.balelecbud.util.http.HttpPostRequest;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;

public class CloudMessagingService extends FirebaseMessagingService implements MessagingService {

    private static final String BASE_URL = getAppContext().getString(R.string.fcm_base_url);
    private static final String FCM_KEY = getAppContext().getString(R.string.fcm_key);

    private static final String TAG = CloudMessagingService.class.getSimpleName();
    private static final MessagingService instance = new CloudMessagingService();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "The token refreshed: " + s);

        TokenUtil.setToken(s);
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
        HttpPostRequest.setAuthorizationKey(FCM_KEY);
        BalelecbudApplication.getHttpClient().post(BASE_URL, send);
    }

    @Override
    public void receiveMessage(RemoteMessage remoteMessage) {
        Map<String, String> message = Message.extractMessage(remoteMessage);
        if(message.isEmpty()) {
            return;
        }
        Log.d(TAG, "About to send notification with title: " + message.get(Message.DATA_KEY_TITLE));
        NotificationMessage.getInstance().scheduleNotification(this, message);
    }

    public static MessagingService getInstance() {
        return instance;
    }
}