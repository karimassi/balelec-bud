package ch.epfl.balelecbud.utility.authentication;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class FirebaseAuthenticatorTest {
    private final Authenticator authenticator = FirebaseAuthenticator.getInstance();

    @Test
    public void signOutMakesUserNull() {
        authenticator.signOut();
        assertNull(authenticator.getCurrentUser());
    }

    @Test
    public void setUserSetsCorrectly() {
        authenticator.signOut();
        User user = new User("mail", "name", "token");
        authenticator.setCurrentUser(user);
        assertEquals(user, authenticator.getCurrentUser());
    }

    @Test
    public void setUserNoEffectWhenSignedIn() {
        authenticator.signOut();
        User user = new User("mail1", "name", "token");
        authenticator.setCurrentUser(user);
        User other = new User("mail2", "name", "token");
        authenticator.setCurrentUser(other);
        assertEquals(user, authenticator.getCurrentUser());
    }
}
