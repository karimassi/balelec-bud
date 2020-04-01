package ch.epfl.balelecbud.authentication;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.User;

public interface Authenticator {

    CompletableFuture<User> signIn(String email, String password);

    CompletableFuture<Void> createAccount(String email, String password);

    void signOut();

    User getCurrentUser();

    String getCurrentUid();

    void setCurrentUser(User user);

}
