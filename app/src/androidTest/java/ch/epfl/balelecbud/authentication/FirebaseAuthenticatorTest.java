package ch.epfl.balelecbud.authentication;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BasicAuthenticationTest;
import ch.epfl.balelecbud.LoginUserActivity;
import ch.epfl.balelecbud.models.User;

@RunWith(AndroidJUnit4.class)
public class FirebaseAuthenticatorTest extends BasicAuthenticationTest {

    @Rule
    public final ActivityTestRule<LoginUserActivity> mActivityRule = new ActivityTestRule<LoginUserActivity>(LoginUserActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            Intents.init();
        }

        @Override
        protected void afterActivityFinished() {
            Intents.release();
        }
    };

    private Authenticator authenticator = MockAuthenticator.getInstance();

    @Before
    public void setUp() throws Throwable{
        mActivityRule.getActivity().setAuthenticator(MockAuthenticator.getInstance());
        logout();
    }

    @Test
    public void signOutMakesUserNull() {
        authenticator.signOut();
        Assert.assertEquals(null, authenticator.getCurrentUser());
    }

    @Test
    public void setUserSetsCorrectly() {
        authenticator.signOut();
        User user = new User("mail", null, "name", "token");
        authenticator.setCurrentUser(user);
        Assert.assertEquals(user, authenticator.getCurrentUser());
    }

    @Test
    public void setUserNoEffectWhenSignedIn() {
        authenticator.signOut();
        User user = new User("mail1", null, "name", "token");
        authenticator.setCurrentUser(user);
        User other = new User("mail2", null, "name", "token");
        authenticator.setCurrentUser(other);
        Assert.assertEquals(user, authenticator.getCurrentUser());
    }

}
