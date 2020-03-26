package ch.epfl.balelecbud.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

public class MockAuthenticator implements Authenticator {

    private static final Authenticator instance = new MockAuthenticator();

    private boolean loggedIn;

    private final Map<String, String> users = new HashMap<String, String>() {
        {
            put("karim@epfl.ch", "123456");
        }
    };

    private static int uid = 1;

    private User currentUser;

    private MockAuthenticator() {
        loggedIn = false;
    }

    @Override
    public CompletableFuture<User> signIn(final String email, final String password) {
        if (users.containsKey(email) && users.get(email).equals(password)) {
            setLoggedIn(true);
            return MockDatabaseWrapper.getInstance().getCustomDocument(DatabaseWrapper.USERS, "0", User.class);
        } else {
            return CompletableFutureUtils.getExceptionalFuture("Failed login");
        }
    }

    @Override
    public CompletableFuture<Void> createAccount(final String email, final String password) {
        if (!users.containsKey(email)) {
            uid++;
            users.put(email, password);
            setLoggedIn(true);
            User u = new User(email, email, String.valueOf(uid));
            return MockDatabaseWrapper.getInstance().storeDocumentWithID(DatabaseWrapper.USERS, u.getUid(), u);
        } else {
            return CompletableFutureUtils.getExceptionalFuture("Failed registration");
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
    public String getCurrentUid() {
        return String.valueOf(uid);
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
