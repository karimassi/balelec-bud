package ch.epfl.balelecbud.utility.cloudMessaging;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

/**
 * Interface modeling a messaging service to communicate with a messaging server
 */
public interface MessagingService {

    /**
     * Send the given {@code JSONObject} to the messaging server. The JSONObject must be of the form :
     * <pre>
     * {
     *   "data" : someData,
     *   "to": destinationToken
     * }
     * </pre>
     *
     * @param send the data to send to the messaging server
     */
    void sendMessage(JSONObject send);

    /**
     * Handle the given message
     *
     * @param remoteMessage an incoming message
     */
    void receiveMessage(RemoteMessage remoteMessage);
}
