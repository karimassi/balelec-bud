package ch.epfl.balelecbud.authentication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import ch.epfl.balelecbud.models.User;
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
    public CompletableFuture<User> signIn(final String email, final String password) {
        if (users.containsKey(email) && users.get(email).equals(password)) {
            setLoggedIn(true);
            return MockDatabaseWrapper.getInstance().getDocument("users", "0", User.class).thenCompose(new Function<User, CompletionStage<User>>() {
                @Override
                public CompletionStage<User> apply(User user) {
                    currentUser = user;
                    return CompletableFuture.completedFuture(user);
                }
            });
        } else {
            return CompletableFuture.completedFuture(null).thenApply(new Function<Object, User>() {
                @Override
                public User apply(Object o) {
                    throw new RuntimeException("Failed login");
                }
            });
        }
    }

    @Override
    public CompletableFuture<Void> createAccount(final String email, final String password) {
        if (!users.containsKey(email)) {
            users.put(email, password);
            setLoggedIn(true);
            User u = new User(email, email, String.valueOf(uid));
            uid++;
            return MockDatabaseWrapper.getInstance().storeDocumentWithID("users", String.valueOf(uid), u);
        } else {
            return CompletableFuture.completedFuture(null).thenApply(new Function<Object, Void>() {
                @Override
                public Void apply(Object o) {
                    throw new RuntimeException("Failed register");
                }
            });
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
