package ch.epfl.balelecbud.utility.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;

public class MockAuthenticator implements Authenticator {

    private static final MockAuthenticator instance = new MockAuthenticator();

    private final Map<String, String> users = new HashMap<String, String>() {
        {
            put("karim@epfl.ch", "123456");
        }
    };

    private static int uid = 0;

    private User currentUser;
    private String currentUserID;

    protected MockAuthenticator() { }

    @Override
    public CompletableFuture<Void> deleteCurrentUser() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<String> signInAnonymously() {
        currentUserID = provideUid();
        return CompletableFuture.completedFuture(currentUserID);
    }

    @Override
    public CompletableFuture<User> signIn(final String email, final String password) {
        if (users.containsKey(email) && Objects.equals(users.get(email), password)) {
            MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, "0"));
            return MockDatabase.getInstance().query(query, User.class).thenApply(users -> users.getList().get(0));
        } else {
            return TestAsyncUtils.getExceptionalFuture("Failed login");
        }
    }

    @Override
    public CompletableFuture<Void> createAccount(final String name, final String email, final String password) {
        if (!users.containsKey(email)) {
            users.put(email, password);
            User u = new User(email, name, currentUserID);
            return MockDatabase.getInstance().storeDocumentWithID(Database.USERS_PATH, u.getUid(), u);
        } else {
            return TestAsyncUtils.getExceptionalFuture("Failed registration");
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
        return currentUserID;
    }

    public static String provideUid() {
        return String.valueOf(uid++);
    }

    @Override
    public void setCurrentUser(User user) {
        if(currentUser == null) {
            currentUserID = user.getUid();
            currentUser = user;
        }
    }

    public static MockAuthenticator getInstance() {
        return instance;
    }
}
