package ch.epfl.balelecbud.authentication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import ch.epfl.balelecbud.util.Callback;


public class FirebaseAuthenticator implements Authenticator {

    private static final Authenticator instance = new FirebaseAuthenticator();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseAuthenticator() {}

    @Override
    public void signIn(String email, String password, final Callback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(getOnSuccessListener(callback))
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public void createAccount(String email, String password, final Callback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(getOnSuccessListener(callback))
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public void signOut() {
        mAuth.signOut();
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public static Authenticator getInstance(){
        return instance;
    }

    private OnSuccessListener getOnSuccessListener(final Callback callback) {
        return new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                callback.onSuccess();
            }
        };
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
