package ch.epfl.balelecbud.authentication;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.TaskToCompletableFutureAdapter;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class FirebaseAuthenticator implements Authenticator {
    private static final Authenticator instance = new FirebaseAuthenticator();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseAuthenticator() { }

    @Override
    public CompletableFuture<User> signIn(String email, String password) {
        return new TaskToCompletableFutureAdapter<>(mAuth.signInWithEmailAndPassword(email, password))
                .thenCompose(authResult -> FirestoreDatabaseWrapper.getInstance()
                        .getCustomDocument(DatabaseWrapper.USERS_PATH,
                                getCurrentUid(), User.class));
    }

    @Override
    public CompletableFuture<Void> createAccount(final String name, final String email, String password) {
        return new TaskToCompletableFutureAdapter<>(mAuth.createUserWithEmailAndPassword(email, password))
                .thenCompose(authResult -> FirestoreDatabaseWrapper.getInstance()
                        .storeDocumentWithID(DatabaseWrapper.USERS_PATH, getCurrentUid(),
                                new User(email, name, getCurrentUid())));
    }

    @Override
    public String getCurrentUid() {
        return mAuth.getCurrentUser().getUid();
    }

    @Override
    public void signOut() {
        Authenticator.super.signOut();
        this.mAuth.signOut();
    }

    public static Authenticator getInstance() {
        return instance;
    }
}
