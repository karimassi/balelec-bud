package ch.epfl.balelecbud.Authentication;

import com.google.android.gms.tasks.OnCompleteListener;

public interface Authenticator {

    void signIn(String email, String password, OnCompleteListener callback);
    void createAccount(String email, String password, OnCompleteListener callback);
    void signOut();
}
