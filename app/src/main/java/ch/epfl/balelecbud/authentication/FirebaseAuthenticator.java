package ch.epfl.balelecbud.authentication;

import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Currency;
import java.util.List;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;


public class FirebaseAuthenticator implements Authenticator {

    private static final Authenticator instance = new FirebaseAuthenticator();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private User currentUser;

    private FirebaseAuthenticator() {
    }

    @Override
    public void signIn(String email, String password, final Callback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirestoreDatabaseWrapper.getInstance()
                                .getDocument(DatabaseWrapper.USERS, mAuth.getCurrentUser().getUid(), User.class, new Callback<User>() {
                                    @Override
                                    public void onSuccess(User data) {
                                        if (data != null) {
                                            callback.onSuccess(data);
                                            Log.d("FirebaseAuthenticator", "Successfully retrieved user from DB");
                                        }
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        callback.onFailure(message);
                                        Log.d("FirebaseAuthenticator", message);

                                    }
                                });
                    }
                })
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public void createAccount(final String email, String password, final Callback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User toStore = new User(email, null, email, mAuth.getCurrentUser().getUid());
                        FirestoreDatabaseWrapper.getInstance()
                                .storeDocumentWithID(DatabaseWrapper.USERS, mAuth.getCurrentUser().getUid(), toStore, new Callback<User>() {
                                    @Override
                                    public void onSuccess(User data) {
                                        callback.onSuccess(data);
                                        Log.d("FirebaseAuthenticator", "Successfully stored user in DB");
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        callback.onFailure(message);
                                    }
                            });
                    }
                })
                .addOnFailureListener(getOnFailureListener(callback));
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

    private OnFailureListener getOnFailureListener(final Callback callback) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getLocalizedMessage());
            }
        };
    }

}
