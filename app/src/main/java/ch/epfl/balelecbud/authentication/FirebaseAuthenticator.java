package ch.epfl.balelecbud.authentication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;
import ch.epfl.balelecbud.util.TaskToCompletableFutureAdapter;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;


public class FirebaseAuthenticator implements Authenticator {

    private static final Authenticator instance = new FirebaseAuthenticator();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private User currentUser;

    private FirebaseAuthenticator() {
    }

    @Override
    public CompletableFuture<User> signIn(String email, String password) {

        return new TaskToCompletableFutureAdapter<>(mAuth.signInWithEmailAndPassword(email, password))
                .thenCompose(new Function<AuthResult, CompletionStage<User>>() {
                    @Override
                    public CompletionStage<User> apply(AuthResult authResult) {
                        return FirestoreDatabaseWrapper.getInstance().getDocument(DatabaseWrapper.USERS, mAuth.getCurrentUser().getUid(), User.class);
                    }
        });
    }

    @Override
    public CompletableFuture<Void> createAccount(final String email, String password) {

        return new TaskToCompletableFutureAdapter<>(mAuth.createUserWithEmailAndPassword(email, password))
                .thenCompose(new Function<AuthResult, CompletionStage<Void>>() {
                    @Override
                    public CompletionStage<Void> apply(AuthResult authResult) {
                        User toStore = new User(email, email, mAuth.getCurrentUser().getUid());
                        setCurrentUser(toStore);
                        FirestoreDatabaseWrapper.getInstance().storeDocumentWithID(DatabaseWrapper.USERS, mAuth.getCurrentUser().getUid(), toStore);
                        return CompletableFuture.completedFuture(null);
                    }
        });
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
    public void setCurrentUser(User user) {
        if (currentUser == null) {
            currentUser = user;
        }
    }

    public static Authenticator getInstance() {
        return instance;
    }

}
