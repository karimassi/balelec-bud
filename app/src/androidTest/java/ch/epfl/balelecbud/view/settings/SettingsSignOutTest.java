package ch.epfl.balelecbud.view.settings;

import androidx.fragment.app.testing.FragmentScenario;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.clickAndCheckDisplay;

public class SettingsSignOutTest {
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    @Before
    public void setUp() {
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        mockAuth.signOut();
        FragmentScenario.launchInContainer(SettingsFragment.class, null, R.style.Theme_AppCompat, null);
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
        clickAndCheckDisplay(
                Lists.newArrayList(R.string.click_to_sign_in, R.string.action_no_account),
                Lists.newArrayList(R.string.register), Lists.newArrayList());
    }

    @Test
    public void whenSignedOutICantDeleteUser() {
        onView(withText(R.string.delete_account)).check(doesNotExist());
    }
}
