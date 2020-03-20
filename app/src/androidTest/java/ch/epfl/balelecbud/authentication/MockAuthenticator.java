package ch.epfl.balelecbud.authentication;

import android.net.Uri;
import android.os.Parcel;
import android.telecom.Call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.internal.firebase_auth.zzff;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

public class MockAuthenticator implements Authenticator {

    private static final Authenticator instance = new MockAuthenticator();

    private boolean loggedIn;

    private final Map<String, String> users = new HashMap<String, String>() {
        {
            put("karim@epfl.ch", "123456");
        }
    };

    private static int uid = 0;

    private User currentUser;

    private MockAuthenticator() {
        loggedIn = false;
    }

    @Override
    public void signIn(final String email, final String password, final Callback callback) {
        if (users.containsKey(email) && users.get(email).equals(password)) {
            setLoggedIn(true);
            MockDatabaseWrapper.getInstance().getDocument("users", "0", User.class, new Callback<User>() {
                @Override
                public void onSuccess(User data) {
                    callback.onSuccess(data);
                }

                @Override
                public void onFailure(String message) {
                    callback.onFailure(message);
                }
            });
        } else {
            callback.onFailure("Login failed");
        }
    }

    @Override
    public void createAccount(final String email, final String password, final Callback callback) {
        if (!users.containsKey(email)) {
            users.put(email, password);
            setLoggedIn(true);
            User u = new User(email, null, email, String.valueOf(uid));
            uid++;
            MockDatabaseWrapper.getInstance().storeDocument("users", u, new Callback<User>() {
                @Override
                public void onSuccess(User data) {
                    callback.onSuccess(data);
                }

                @Override
                public void onFailure(String message) {
                    callback.onFailure(message);
                }
            });
        } else {
            callback.onFailure("Registration failed: account already exists with this email");
        }
    }

    @Override
    public void signOut() {
        setLoggedIn(false);
        currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User user) {
        if(currentUser == null) {
            currentUser = user;
        }
    }

    private void setLoggedIn(boolean state) {
        loggedIn = state;
    }

    public static Authenticator getInstance() {
        return instance;
    }

    public void debugSetUser(User user) {
        currentUser = user;
    }


}
