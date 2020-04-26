package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class Message {

    private static final String TAG = CloudMessagingService.class.getSimpleName();

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";

    public static final String MESSAGE_TYPE_GENERAL = "GENERAL";
    public static final String DATA_KEY_TITLE = "title";
    public static final String DATA_KEY_BODY = "body";
    public static final String DATA_KEY_TYPE = "type";

    private static String token = null;

    private String type;
    private String title;
    private String body;

    public Message(String type, String title, String body) {
        this.type = type;
        this.title = title;
        this.body = body;
    }

    public void sendMessage(String uid) {
        Log.d(TAG, "In send message, uid: " + uid);

        BalelecbudApplication.getAppDatabaseWrapper()
                .getDocument(FirestoreDatabaseWrapper.TOKENS_PATH, uid)
                .whenCompleteAsync((t, throwable) -> {
                    if( t != null ) {
                        String token = (String) t.get("token");
                        Log.d(TAG, "In send message, token: " + token);

                        JSONObject send = new JSONObject();
                        JSONObject message = new JSONObject();

                        try {
                            message.put(DATA_KEY_TYPE, type)
                                    .put(DATA_KEY_TITLE, title)
                                    .put(DATA_KEY_BODY, body);
                            send.put("data", message).put("to", token);
                            BalelecbudApplication.getHttpClient().post(BASE_URL, send);
                        } catch (JSONException e) {
                            Log.d(TAG, "Couldn't put data in JSONObj");
                            e.printStackTrace();
                        }
                    }
                    else Log.d(TAG,
                            "Didn't find token for this user, stopped sending the message");
                });
    }

    public static void setTokenToDatabase() {
        User user = getAppAuthenticator().getCurrentUser();
        if(user != null) {
            String uid = user.getUid();
            Log.d(TAG, "Storing token in  database for user: " + uid);
            Log.d(TAG, "Token: " + token);
            if(token != null && uid != null) {
                Map<String, String> toStore = new HashMap<>();
                toStore.put("token", token);
                getAppDatabaseWrapper().storeDocumentWithID(DatabaseWrapper.TOKENS_PATH, uid, toStore);
                Log.d(TAG, "Stored the new token");
                token = null;
            }
        }
    }

    static void setToken(String newToken) {
        if(newToken != null) {
            Log.d(TAG, "Storing new token temporarily");
            token = newToken;
        }
    }

    @VisibleForTesting
    protected static String getToken() {
        return token;
    }
}