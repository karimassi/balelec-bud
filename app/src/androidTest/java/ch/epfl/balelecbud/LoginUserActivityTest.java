package ch.epfl.balelecbud;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.Activities.LoginUserActivity;
import ch.epfl.balelecbud.Activities.RegisterUserActivity;
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
public class LoginUserActivityTest {
    @Rule
    public final ActivityTestRule<LoginUserActivity> mActivityRule =
            new ActivityTestRule<>(LoginUserActivity.class);

    private IdlingResource mActivityResource;

    @Before
    public void setUp() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuthenticator.getInstance().signOut();
        }
        ActivityScenario activityScenario = ActivityScenario.launch(LoginUserActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<LoginUserActivity>() {
            @Override
            public void perform(LoginUserActivity activity) {
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
    public void testCantSignInEmptyFields() {
        // empty email empty password
        enterEmail("");
        enterPassword("");
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Email required!")));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
    }
//
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
        onView(withId(R.id.buttonSignOut)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToRegister() {
        onView(withId(R.id.buttonLoginToRegister)).perform(click());
        onView(withId(R.id.editTextEmailRegister)).check(matches(isDisplayed()));
    }

    private void enterEmail(String email) {
        onView(withId(R.id.editTextEmailLogin)).perform(typeText(email)).perform(closeSoftKeyboard());
    }

    private void enterPassword(String password) {
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText(password)).perform(closeSoftKeyboard());
    }

}