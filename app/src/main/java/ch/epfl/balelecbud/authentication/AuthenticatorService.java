package ch.epfl.balelecbud.authentication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import ch.epfl.balelecbud.models.User;

public class AuthenticatorService extends Service {
    private static final String TAG = AuthenticatorService.class.getSimpleName();
    private static final String CURRENT_USER = TAG + ".CURRENT_USER";
    private static Authenticator authenticator = FirebaseAuthenticator.getInstance();
    private IBinder binder = new AuthenticatorBinder();

    public static class AuthenticatorBinder extends Binder {
        public Authenticator getAuthenticator() {
            return AuthenticatorService.authenticator;
        }
    }

    @VisibleForTesting
    public void setAuthenticator(Authenticator authenticator) {
        AuthenticatorService.authenticator = authenticator;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = this.getSharedPreferences(AuthenticatorService.CURRENT_USER, Context.MODE_PRIVATE);
        User currentUser = User.restoreUser(preferences);
        if (currentUser != null) {
            authenticator.setCurrentUser(currentUser);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (authenticator.getCurrentUser() != null) {
            storeUser(authenticator.getCurrentUser());
        }
    }

    private void storeUser(User user) {
        SharedPreferences preferences = this.getSharedPreferences(AuthenticatorService.CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        user.storeUser(editor);
        editor.apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
