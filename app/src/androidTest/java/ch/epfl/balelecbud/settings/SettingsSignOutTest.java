package ch.epfl.balelecbud.settings;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SettingsSignOutTest {
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<SettingsActivity>(SettingsActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            BalelecbudApplication.setAppAuthenticator(mockAuth);
            mockAuth.signOut();
        }
    };

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
