package ch.epfl.balelecbud;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class BasicActivity extends AppCompatActivity {

    private static Authenticator authenticator = FirebaseAuthenticator.getInstance();
    private static DatabaseWrapper databaseWrapper = FirestoreDatabaseWrapper.getInstance();

    @VisibleForTesting
    public static void setAuthenticator(Authenticator auth) {
        authenticator = auth;
    }

    @VisibleForTesting
    public static void setDatabase(DatabaseWrapper db) {
        databaseWrapper = db;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public DatabaseWrapper getDatabase() {
        return databaseWrapper;
    }

}
