package ch.epfl.balelecbud;

import android.Manifest;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginUserActivityTest extends BasicAuthenticationTest{

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    );

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

    @Before
    public void setUp() throws Throwable{
        LoginUserActivity.setAuthenticator(MockAuthenticator.getInstance());
        logout();
    }

    @Test
    public void testCantSignInEmptyFields() {
        // empty email empty password
        enterEmail("");
        enterPassword("");
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Email required!")));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
    }

    @Test
    public void testCantSignInInvalidEmailEmptyPassword() {
        // invalid email empty pws
        enterEmail("fakemail");
        enterPassword("");
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
    }

    @Test
    public void testCantSignInvalidEmailEmptyPassword() {
        // valid email empty pws
        enterEmail("fakemail@correct.ch");
        enterPassword("");
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
    }

    @Test
    public void testCantSignInInvalidEmail() {
        // invalid email non-empty pws
        enterEmail("fakemail");
        enterPassword("1234");
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
    }

    @Test
    public void testValidNoExistingAccount() {
        enterEmail("doesnotexist@gmail.ch");
        enterPassword("123456");
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanLogIn() {
        enterEmail("karim@epfl.ch");
        enterPassword("123456");
        onView(withId(R.id.buttonLogin)).perform(click());
        intended(hasComponent(WelcomeActivity.class.getName()));
    }

    @Test
    public void testGoToRegister() {
        onView(withId(R.id.buttonLoginToRegister)).perform(click());
        intended(hasComponent(RegisterUserActivity.class.getName()));
    }

    private void enterEmail(String email) {
        onView(withId(R.id.editTextEmailLogin)).perform(typeText(email)).perform(closeSoftKeyboard());
    }

    private void enterPassword(String password) {
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText(password)).perform(closeSoftKeyboard());
    }

}