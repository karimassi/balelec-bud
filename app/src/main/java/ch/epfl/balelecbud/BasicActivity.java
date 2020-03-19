package ch.epfl.balelecbud;

import android.provider.ContactsContract;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class BasicActivity extends AppCompatActivity {

    private Authenticator authenticator = FirebaseAuthenticator.getInstance();

    private DatabaseWrapper databaseWrapper = FirestoreDatabaseWrapper.getInstance();

    @VisibleForTesting
    public void setAuthenticator(Authenticator auth) {
        authenticator = auth;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

}
