package ch.epfl.balelecbud.settings;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LoginUserTest {
    private MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
                    mockAuth.signOut();
                }
            };

    private void openFragment() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_settings));
        device.waitForIdle();
    }

    @Before
    public void setUp() {
        openFragment();
        onView(withText(R.string.not_sign_in)).perform(click());
    }

    @Test
    public void testCantSignInEmptyFields() {
        // empty email empty password
        enterEmail("");
        enterPassword("");
        onView(withText(R.string.action_sign_in)).perform(click());
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCantSignInInvalidEmailEmptyPassword() {
        // invalid email empty pws
        enterEmail("fakemail");
        enterPassword("");
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
        onView(withText(R.string.action_sign_in)).perform(click());
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCantSignInvalidEmailEmptyPassword() {
        // valid email empty pws
        enterEmail("fakemail@correct.ch");
        enterPassword("");
        onView(withId(R.id.editTextPasswordLogin)).check(matches(hasErrorText("Password required!")));
        onView(withText(R.string.action_sign_in)).perform(click());
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCantSignInInvalidEmail() {
        // invalid email non-empty pws
        enterEmail("fakemail");
        enterPassword("1234");
        onView(withId(R.id.editTextEmailLogin)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withText(R.string.action_sign_in)).perform(click());
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testValidNoExistingAccount() {
        enterEmail("doesnotexist@gmail.ch");
        enterPassword("123456");
        onView(withText(R.string.action_sign_in)).perform(click());
        onView(withText(R.string.not_sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanLogIn() {
        enterEmail("karim@epfl.ch");
        enterPassword("123456");
        onView(withText(R.string.action_sign_in)).perform(click());
        assertNotNull(mockAuth.getCurrentUser());
        onView(withText(R.string.sign_out_text));
    }

    @Test
    public void testGoToRegister() {
        onView(withText(R.string.action_no_account)).perform(click());
        onView(allOf(withText(R.string.register), not(isClickable()))).check(matches(isDisplayed()));
    }

    private void enterEmail(String email) {
        onView(withId(R.id.editTextEmailLogin)).perform(typeText(email)).perform(closeSoftKeyboard());
    }

    private void enterPassword(String password) {
        onView(withId(R.id.editTextPasswordLogin)).perform(typeText(password)).perform(closeSoftKeyboard());
    }
}