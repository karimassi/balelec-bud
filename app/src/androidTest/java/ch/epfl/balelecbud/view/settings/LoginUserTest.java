package ch.epfl.balelecbud.view.settings;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.testUtils.CustomMatcher;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppAuthenticator;
import static ch.epfl.balelecbud.utility.database.MockDatabase.karim;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoginUserTest {
    private MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private FragmentScenario<SettingsFragment> scenario;

    @Before
    public void setUp() {
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
        mockAuth.signOut();
        scenario = FragmentScenario.launchInContainer(SettingsFragment.class, null, R.style.Theme_AppCompat, null);
        onView(withText(R.string.not_sign_in)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).perform(clearText());
        onView(withId(R.id.editTextPasswordLogin)).perform(clearText());
    }

    @Test
    public void testCantSignInEmptyFields() {
        // empty email empty password
        enterAndClick("", "");
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCantSignInInvalidEmailEmptyPassword() {
        // invalid email empty pws
        enterAndCheckErrors("fakemail", "", "Enter a valid email!", "Password required!");
    }

    @Test
    public void testCantSignInvalidEmailEmptyPassword() {
        // valid email empty pws
        enterAndCheckErrors("fakemail@correct.ch", "", null, "Password required!");
    }

    @Ignore("Redundant")
    @Test
    public void testCantSignInInvalidEmail() {
        // invalid email non-empty pws
        enterAndCheckErrors("fakemail", "123456", "Enter a valid email!", null);
    }

    private void enterAndCheckErrors(String email, String pwd, String emailErrorText, String pwdErrorText) {
        enterAndClick(email, pwd);
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText(emailErrorText)));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText(pwdErrorText)));
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testValidNoExistingAccount() {
        enterAndClick("doesnotexist@gmail.ch", "123456");
        onView(withText(R.string.not_sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanLogIn() {
        enterAndClick("karim@epfl.ch", "123456");
        assertNotNull(mockAuth.getCurrentUser());
        onView(withText(R.string.sign_out_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToRegister() {
        onView(withText(R.string.action_no_account)).perform(click());
        onView(allOf(withText(R.string.register), not(isClickable()))).check(matches(isDisplayed()));
    }

    @Test
    public void testConnectingIsDisplayedWhenWaiting() {
        CompletableFuture<User> future = new CompletableFuture<>();
        setAppAuthenticator(new MockAuthenticator() {
            @Override
            public CompletableFuture<User> signIn(String email, String password) {
                return future;
            }
        });
        enterAndClick("karim@epfl.ch", "123456");
        onView(withText(R.string.connecting)).check(matches(isDisplayed()));
        assertTrue(future.complete(karim));
        onView(withText(R.string.sign_out_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testMessagesIsDisplayedWhenLoginFailed() {
        setAppAuthenticator(new MockAuthenticator() {
            @Override
            public CompletableFuture<User> signIn(String email, String password) {
                return TestAsyncUtils.getExceptionalFuture("Failed to login");
            }
        });
        enterAndClick("karim@epfl.ch", "123456");
        onView(withText(R.string.sign_in_failed))
                .inRoot(CustomMatcher.isToast())
                .check(matches(isDisplayed()));
    }

    private void enterAndClick(String email, String pwd) {
        onView(withId(R.id.editTextEmailLogin)).perform(typeText(email)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText(pwd)).perform(closeSoftKeyboard());
        onView(withText(R.string.action_sign_in)).perform(click());
    }
}