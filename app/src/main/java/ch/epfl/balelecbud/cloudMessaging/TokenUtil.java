package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.Database;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public class TokenUtil {

    private static final String TAG = TokenUtil.class.getSimpleName();

    private static String token = null;

    public static void storeToken() {
        User user = getAppAuthenticator().getCurrentUser();
        if(user != null) {
            String uid = user.getUid();
            Log.d(TAG, "Storing token in  database for user: " + uid);
            Log.d(TAG, "Token: " + token);
            if(token != null && uid != null) {
                Map<String, Boolean> toStore = new HashMap<>();
                toStore.put(token, true);
                getAppDatabase().storeDocumentWithID(Database.TOKENS_PATH, uid, toStore);
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
    static String getToken() {
        return token;
    }
}