package ch.epfl.balelecbud.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

public class MockAuthenticator implements Authenticator {

    private static final MockAuthenticator instance = new MockAuthenticator();

    private final Map<String, String> users = new HashMap<String, String>() {
        {
            put("karim@epfl.ch", "123456");
        }
    };

    private static int uid = 0;

    private User currentUser;

    private MockAuthenticator() { }

    @Override
    public CompletableFuture<User> signIn(final String email, final String password) {
        if (users.containsKey(email) && Objects.equals(users.get(email), password)) {
            return MockDatabaseWrapper.getInstance().getCustomDocument(DatabaseWrapper.USERS_PATH, "0", User.class);
        } else {
            return CompletableFutureUtils.getExceptionalFuture("Failed login");
        }
    }

    @Override
    public CompletableFuture<Void> createAccount(final String name, final String email, final String password) {
        if (!users.containsKey(email)) {
            users.put(email, password);
            User u = new User(email, email, provideUid());
            return MockDatabaseWrapper.getInstance().storeDocumentWithID(DatabaseWrapper.USERS_PATH, u.getUid(), u);
        } else {
            return CompletableFutureUtils.getExceptionalFuture("Failed registration");
        }
    }

    @Override
    public void signOut() {
        currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public String getCurrentUid() {
        return String.valueOf(uid-1);
    }

    public static String provideUid() {
        return String.valueOf(uid++);
    }

    @Override
    public void setCurrentUser(User user) {
        if(currentUser == null) {
            currentUser = user;
        }
    }

    public static MockAuthenticator getInstance() {
        return instance;
    }
}
