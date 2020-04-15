package ch.epfl.balelecbud.cloudMessaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.epfl.balelecbud.MainActivity;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;

public class CloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "CloudMessaging.CloudMessagingService";
    public static final String SEND_MESSAGE_ACTION = "SEND_MESSAGE_ACTION";
    public static final String SEND_NOTIFICATION_ACTION = "SEND_NOTIFICATION_ACTION";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "The token refreshed: " + s);

        FirebaseAuthenticator.getInstance().getCurrentUser().setToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //TODO: token to uid
        String senderUid = remoteMessage.getFrom();

        if(remoteMessage.getData().size() > 0) {
            //TODO: set message type + get => idea: do a class w/ fun that do the notif by type + var
            String messageReceived = remoteMessage.getData().get("message");

            Log.d(TAG, "Received message: " + messageReceived);

            passMessageToActivity(messageReceived, senderUid);
        }
        else if(remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Received notification: " + title);

            passNotificationToActivity(title, body, senderUid);
        }
    }

    private void passMessageToActivity(String message, String uid) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("message", message)
                .putExtra("from", uid)
                .setAction(SEND_MESSAGE_ACTION);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void passNotificationToActivity(String title, String body, String uid) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("title", title)
                .putExtra("body", body)
                .putExtra("from", uid)
                .setAction(SEND_NOTIFICATION_ACTION);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
