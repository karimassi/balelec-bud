package ch.epfl.balelecbud;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;

public class BasicActivity extends AppCompatActivity {

    private static Authenticator authenticator = FirebaseAuthenticator.getInstance();

    @VisibleForTesting
    public static void setAuthenticator(Authenticator auth) {
        authenticator = auth;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

}
