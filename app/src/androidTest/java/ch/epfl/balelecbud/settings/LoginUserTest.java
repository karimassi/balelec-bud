package ch.epfl.balelecbud.settings;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LoginUserTest {
    private MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    @Rule
    public final ActivityTestRule<SettingsActivity> mActivityRule =
            new ActivityTestRule<SettingsActivity>(SettingsActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppAuthenticator(mockAuth);
            mockAuth.signOut();
        }
    };

    @Before
    public void setUp() {
        onView(withText(R.string.not_sign_in)).perform(click());
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
        assertNotNull(mockAuth.getCurrentUser());
        onView(withText(mActivityRule.getActivity().getString(R.string.sign_out_text)));
    }

    @Test
    public void testGoToRegister() {
        onView(withId(R.id.buttonLoginToRegister)).perform(click());
        onView(withText(R.string.register)).check(matches(isDisplayed()));
    }

    private void enterEmail(String email) {
        onView(withId(R.id.editTextEmailLogin)).perform(typeText(email)).perform(closeSoftKeyboard());
    }

    private void enterPassword(String password) {
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText(password)).perform(closeSoftKeyboard());
    }
}