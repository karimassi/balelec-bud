package ch.epfl.balelecbud.cloudMessaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class CloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = CloudMessagingService.class.getSimpleName();
    public static final String SEND_NOTIFICATION_ACTION = "SEND_NOTIFICATION_ACTION";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "The token refreshed: " + s);

        String currentUid = getAppAuthenticator().getCurrentUid();

        getAppDatabaseWrapper().getCustomDocument(DatabaseWrapper.TOKENS_PATH,
                    currentUid, String.class).whenComplete((t, throwable) -> {
                        if(t!= null) {
                            getAppDatabaseWrapper()
                                    .deleteDocumentWithID(DatabaseWrapper.TOKENS_PATH, currentUid);
                        }
        });

        getAppDatabaseWrapper().storeDocumentWithID(DatabaseWrapper.TOKENS_PATH, currentUid, s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "";
        String body = "";
        String type = "";

        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Received Message");
            title = remoteMessage.getData().get(Message.DATA_KEY_TITLE);
            body = remoteMessage.getData().get(Message.DATA_KEY_BODY);
            type = remoteMessage.getData().get(Message.DATA_KEY_TYPE);
        }
        else if(remoteMessage.getNotification() != null) {
            Log.d(TAG, "Received Notification");
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }
        passNotificationToActivity(title, body, type);
    }

    private void passNotificationToActivity(String title, String body, String type) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra(Message.DATA_KEY_TITLE, title)
                .putExtra(Message.DATA_KEY_BODY, body)
                .putExtra(Message.DATA_KEY_TYPE, type)
                .setAction(SEND_NOTIFICATION_ACTION);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
