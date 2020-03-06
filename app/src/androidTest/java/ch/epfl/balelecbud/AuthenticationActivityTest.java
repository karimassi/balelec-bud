package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.Activities.AuthenticationActivity;
import ch.epfl.balelecbud.Authentication.FirebaseAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class AuthenticationActivityTest {
    @Rule
    public final ActivityTestRule<AuthenticationActivity> mActivityRule =
            new ActivityTestRule<>(AuthenticationActivity.class);

    @Before
    public void setUp() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuthenticator.getInstance().signOut();
            user.delete();
        }
    }

    @After
    public void tearDown() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuthenticator.getInstance().signOut();
            user.delete();
        }
    }

    @Test
    public void testCantSignInEmptyFields() {
        // empty email empty password
        onView(withId(R.id.editTextEmailLogin)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Email required!")));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
    }

    @Test
    public void testCantSignInInvalidEmailEmptyPassword() {
        // invalid email empty pws
        onView(withId(R.id.editTextEmailLogin)).perform(typeText("fakemail")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
    }

    @Test
    public void testCantSignInvalidEmailEmptyPassword() {
        // valid email empty pws
        onView(withId(R.id.editTextEmailLogin)).perform(typeText("fakemail@correct.ch")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
    }

    @Test
    public void testCantSignInInvalidEmail() {
        // invalid email non-empty pws
        onView(withId(R.id.editTextEmailLogin)).perform(typeText("fakemail")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText("1234")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
    }

    @Test
    public void testValidInputsButFails() {
        onView(withId(R.id.editTextEmailLogin)).perform(typeText("fakemail@correct.ch")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText("123456")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());
        Assert.assertTrue(FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    @Test
    public void testGoToRegister() {
        onView(withId(R.id.buttonLoginToRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(isDisplayed()));
    }

}