package ch.epfl.balelecbud.utility.cloudMessaging;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public interface MessagingService {

    void sendMessage(JSONObject send);

    void receiveMessage(RemoteMessage remoteMessage);
}
