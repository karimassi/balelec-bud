package ch.epfl.balelecbud;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

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

    private IdlingResource mActivityResource;

    @Before
    public void setUp() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuth.getInstance().signOut();
        }
        ActivityScenario activityScenario = ActivityScenario.launch(RegisterUserActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<RegisterUserActivity>() {
            @Override
            public void perform(RegisterUserActivity activity) {
                mActivityResource = activity.getIdlingResource();
                IdlingRegistry.getInstance().register(mActivityResource);
            }
        });
    }

    @After
    public void tearDown() {
        if (mActivityResource != null) {
            IdlingRegistry.getInstance().unregister(mActivityResource);
        }
    }

    @Test
    public void testCantRegisterWithEmptyFields() {
        // empty email empty password
        enterEmail("");
        enterPassword("");
        repeatPassword("");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Email required!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterInvalidEmailEmptyPassword() {
        // invalid email empty pwd
        enterEmail("invalidemail");
        enterPassword("");
        repeatPassword("");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterValidEmailEmptyPassword() {
        // valid email empty pwd
        enterEmail("invalid@email.com");
        enterPassword("");
        repeatPassword("");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterInvalidEmail() {
        // invalid email valid pwd
        enterEmail("invalidemail");
        enterPassword("123456");
        repeatPassword("123456");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
    }

    @Test
    public void testCantRegisterMismatchPassword() {
        // invalid email valid pwd
        enterEmail("invalidemail");
        enterPassword("123456");
        repeatPassword("123478");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
    }

    @Test
    public void testCantRegisterInvalidEmailShortPassword() {
        // invalid email invalid password
        enterEmail("invalidemail");
        enterPassword("124");
        repeatPassword("124");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password should be at least 6 characters long.")));
    }

    @Test
    public void testRegisterExistingAccount() {
        enterEmail("karim@epfl.ch");
        enterPassword("123456");
        repeatPassword("123456");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.buttonRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToLogin() {
        onView(withId(R.id.buttonRegisterToLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanRegister() {
        enterEmail("testregister" + randomInt() + "@gmail.com");
        enterPassword("123123");
        repeatPassword("123123");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.greetingTextView)).check(matches(isDisplayed()));
    }

    private void enterEmail(String email) {
        onView(withId(R.id.editTextEmailRegister)).perform(typeText(email)).perform(closeSoftKeyboard());
    }

    private void enterPassword(String password) {
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText(password)).perform(closeSoftKeyboard());
    }

    private void repeatPassword(String password) {
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText(password)).perform(closeSoftKeyboard());
    }

    private String randomInt() {
        return String.valueOf(new Random().nextInt(10000));
    }


}