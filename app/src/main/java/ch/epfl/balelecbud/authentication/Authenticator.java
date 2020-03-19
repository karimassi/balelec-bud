package ch.epfl.balelecbud.authentication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.balelecbud.util.Callback;

public interface Authenticator {

    void signIn(String email, String password, Callback callback);
    void createAccount(String email, String password, Callback callback);
    void signOut();
    FirebaseUser getCurrentUser();

}
