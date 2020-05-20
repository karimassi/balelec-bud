package ch.epfl.balelecbud.utility.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.User;

/**
 * Interface to model the authentication mechanism
 */
public interface Authenticator {
    String CURRENT_USER = "Authenticator.CURRENT_USER";

    /**
     * Delete the current user
     *
     * @return a {@code CompletableFuture} that will complete when the deletion is complete
     */
    CompletableFuture<Void> deleteCurrentUser();

    /**
     * Sign in anonymously
     *
     * @return a {@code CompletableFuture} that will complete with the new user ID
     */
    CompletableFuture<String> signInAnonymously();

    /**
     * Sign in with an email and a password
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return         a {@code CompletableFuture} that will complete with the user
     */
    CompletableFuture<User> signIn(String email, String password);

    /**
     * Create a new account and sign in
     *
     * @param name     the name of the new user
     * @param email    the email of the new user
     * @param password the password of the new user
     * @return         a {@code CompletableFuture} that will complete with the new user
     */
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
