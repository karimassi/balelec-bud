package ch.epfl.balelecbud;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.Authentication.Authenticator;
import ch.epfl.balelecbud.Authentication.FirebaseAuthenticator;

public class BasicActivity extends AppCompatActivity {

    private Authenticator authenticator = FirebaseAuthenticator.getInstance();

    protected void setAuthenticator(Authenticator auth) {
        authenticator = auth;
    }

    protected Authenticator getAuthenticator() {
        return authenticator;
    }

}
