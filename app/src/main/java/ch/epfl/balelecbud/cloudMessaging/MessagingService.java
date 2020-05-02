package ch.epfl.balelecbud.cloudMessaging;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public interface MessagingService {

    void sendMessage(JSONObject send);

    void receiveMessage(RemoteMessage remoteMessage);
}
