package ch.epfl.balelecbud;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.AuthenticatorService;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class BasicActivity extends AppCompatActivity {

    private Authenticator authenticator = FirebaseAuthenticator.getInstance();
    private static DatabaseWrapper databaseWrapper = FirestoreDatabaseWrapper.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AuthenticatorService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unbindService(connection);
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BasicActivity.this.authenticator =
                    ((AuthenticatorService.AuthenticatorBinder) service).getAuthenticator();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
