package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import ch.epfl.balelecbud.Activities.RegisterUserActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class RegisterUserActivityTest {
    @Rule
    public final ActivityTestRule<RegisterUserActivity> mActivityRule =
            new ActivityTestRule<>(RegisterUserActivity.class);

    @Before
    public void setUp() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuth.getInstance().signOut();
            user.delete();
        }
    }

    @After
    public void tearDown() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuth.getInstance().signOut();
            user.delete();
        }
    }

    @Test
    public void testCantRegister1() {
        // empty email empty password
        onView(withId(R.id.editTextEmailRegister)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Email required!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegister2() {
        // invalid email empty pws
        onView(withId(R.id.editTextEmailRegister)).perform(typeText("fakemail")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegister3() {
        // valid email empty pws
        onView(withId(R.id.editTextEmailRegister)).perform(typeText("fakemail@correct.ch")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegister4() {
        // invalid email non-empty pws
        onView(withId(R.id.editTextEmailRegister)).perform(typeText("fakemail")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText("123456")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText("123546")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Passwords do not match!")));
    }

    @Test
    public void testCantRegister5() {
        // invalid email invalid password
        onView(withId(R.id.editTextEmailRegister)).perform(typeText("fakemail")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText("126")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText("126")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password should be at least 6 characters long.")));
    }

    @Test
    public void testRegisterFails() {
        onView(withId(R.id.editTextEmailRegister)).perform(typeText("karim@epfl.ch")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText("123456")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText("123456")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.buttonRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterCorrectly() {
        onView(withId(R.id.editTextEmailRegister)).perform(typeText("testregister@epfl.ch")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText("123456")).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText("123456")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonRegister)).perform(click());
    }

    @Test
    public void testGoToLogin() {
        onView(withId(R.id.buttonRegisterToLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
    }

}