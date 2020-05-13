package ch.epfl.balelecbud.utility.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.User;

public interface Authenticator {
    String CURRENT_USER = "Authenticator.CURRENT_USER";

    CompletableFuture<Void> deleteCurrentUser();

    CompletableFuture<String> signInAnonymously();

    CompletableFuture<User> signIn(String email, String password);

    CompletableFuture<Void> createAccount(String name, String email, String password);

    default void signOut() {
        SharedPreferences preferences = BalelecbudApplication
                .getAppContext().getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.clear();
        editor.apply();
    }

    default User getCurrentUser() {
        SharedPreferences preferences = BalelecbudApplication
                .getAppContext().getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        return User.restoreUser(preferences);
    }

    String getCurrentUid();

    default void setCurrentUser(User user) {
        SharedPreferences preferences = BalelecbudApplication
                .getAppContext().getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        if (!User.isAUserStored(preferences)) {
            SharedPreferences.Editor editor = preferences.edit();
            user.storeUser(editor);
            editor.apply();
        }
    }
}
