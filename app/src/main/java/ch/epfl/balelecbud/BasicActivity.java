package ch.epfl.balelecbud;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;

public class BasicActivity extends AppCompatActivity {

    private Authenticator authenticator = FirebaseAuthenticator.getInstance();

    protected void setAuthenticator(Authenticator auth) {
        authenticator = auth;
    }

    protected Authenticator getAuthenticator() {
        return authenticator;
    }

}
