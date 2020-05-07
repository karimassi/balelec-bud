package ch.epfl.balelecbud.settings;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LoginUserTest {
    private MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    @Before
    public void setUp() {
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
        mockAuth.signOut();
        FragmentScenario.launchInContainer(SettingsFragment.class, null, R.style.Theme_AppCompat, null);
        onView(withText(R.string.not_sign_in)).perform(click());
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
        enterAndClick("fakemail", "");
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCantSignInvalidEmailEmptyPassword() {
        // valid email empty pws
        enterAndClick("fakemail@correct.ch", "");
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCantSignInInvalidEmail() {
        // invalid email non-empty pws
        enterAndClick("fakemail", "1234");
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
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

    private void enterAndClick(String email, String pwd) {
        onView(withId(R.id.editTextEmailLogin)).perform(typeText(email)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText(pwd)).perform(closeSoftKeyboard());
        onView(withText(R.string.action_sign_in)).perform(click());
    }
}