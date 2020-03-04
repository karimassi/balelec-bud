package ch.epfl.balelecbud.Model;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;


public class FirebaseAuthenticator implements Authenticator {

    private FirebaseAuth mAuth;

    public FirebaseAuthenticator() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signIn(String email, String password, OnCompleteListener callback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    @Override
    public void createAccount(String email, String password, OnCompleteListener callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    @Override
    public void signOut() {
        mAuth.signOut();
    }
}
