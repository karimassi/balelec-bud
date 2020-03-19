package ch.epfl.balelecbud.authentication;

import com.google.firebase.auth.FirebaseUser;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;

public interface Authenticator {

    void signIn(String email, String password, Callback callback);

    void createAccount(String email, String password, Callback callback);

    void signOut();

    User getCurrentUser();

    void setCurrentUser(User user);

}
