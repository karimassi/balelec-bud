package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.notifications.NotificationGeneral;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class CloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = CloudMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "The token refreshed: " + s);

        String uid = getAppAuthenticator().getCurrentUser().getUid();
        getAppDatabaseWrapper().getDocument(DatabaseWrapper.TOKENS_PATH,
                uid).whenCompleteAsync((t, throwable) -> {
            if (t.get("token") != null) {
                getAppDatabaseWrapper().deleteDocumentWithID(DatabaseWrapper.TOKENS_PATH, uid);
            }
        });
        getAppDatabaseWrapper().storeDocumentWithID(DatabaseWrapper.TOKENS_PATH, uid, s);
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
        }
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Received Notification");
            message.put(Message.DATA_KEY_TITLE, remoteMessage.getNotification().getTitle());
            message.put(Message.DATA_KEY_BODY, remoteMessage.getNotification().getBody());
            message.put(Message.DATA_KEY_TYPE, "default");
        }
        sendNotification(message);
    }

    private void sendNotification(Map<String, String> message) {
        switch (message.get(Message.DATA_KEY_TYPE)) {
            case Message.MESSAGE_TYPE_FRIENDSHIP:
                NotificationGeneral.getInstance()
                        .scheduleNotification(this, message);
            case Message.MESSAGE_TYPE_FRIEND_REQUEST:
                NotificationGeneral.getInstance()
                        .scheduleNotification(this, message);
            default: break;
        }
    }
}
