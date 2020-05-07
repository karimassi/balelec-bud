package ch.epfl.balelecbud.settings;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class SettingsSignOutTest {
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    mockAuth.signOut();
                }
            };

    @Before
    public void openFragment() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_settings));
        device.waitForIdle();
    }

    @Test
    public void whenSignedOutSignOutIsNotDisplayed() {
        onView(withText(R.string.sign_out_text)).check(doesNotExist());
    }

    @Test
    public void whenSignedOutSignInIsDisplayed() {
        onView(withText(R.string.click_to_sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void whenSignedOutICanSignIn() {
        onView(withText(R.string.click_to_sign_in)).perform(click());
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void whenSignedOutICanRegister() {
        onView(withText(R.string.click_to_sign_in)).perform(click());
        onView(withText(R.string.action_no_account)).perform(click());
        onView(withText(R.string.register)).check(matches(isDisplayed()));
    }
}
