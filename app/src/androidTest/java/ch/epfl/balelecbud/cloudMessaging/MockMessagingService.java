package ch.epfl.balelecbud.cloudMessaging;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.notifications.NotificationMessage;

public class MockMessagingService implements MessagingService {

    private static final String TAG = MockMessagingService.class.getSimpleName();
    private static final MockMessagingService instance = new MockMessagingService();
    private Context context;

    private MockMessagingService() {

    }

    @Override
    public void sendMessage(JSONObject send) {
        Map<String, String> message = new HashMap<>();
        try {
            JSONObject data = (JSONObject) send.get("data");
            message.put(Message.DATA_KEY_TITLE, (String) data.get(Message.DATA_KEY_TITLE));
            message.put(Message.DATA_KEY_BODY, (String) data.get(Message.DATA_KEY_BODY));
            RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message)
                    .setMessageType((String) data.get(Message.DATA_KEY_TYPE)).build();
            this.receiveMessage(rm);
        } catch (JSONException e) {
            Log.d(TAG, "Couldn't get information from JSONObject");
        }
    }

    @Override
    public void receiveMessage(RemoteMessage remoteMessage) {
        Map<String, String> message = Message.extractRemoteMessage(remoteMessage);
        if(message.isEmpty()) {
            return;
        }
        Log.d(TAG, "About to send notification with title: " + message.get(Message.DATA_KEY_TITLE));
        NotificationMessage.getInstance().scheduleNotification(context, message);
    }

    public static MockMessagingService getInstance() {
        return instance;
    }

    void setContext(Context context) {
        this.context = context;
    }
}