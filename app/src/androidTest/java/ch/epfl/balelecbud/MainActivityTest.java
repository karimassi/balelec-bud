package ch.epfl.balelecbud;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    MockAuthenticator.getInstance().signOut();
                    Intents.init();
                }

                @Override
                protected void afterActivityFinished() {
                    Intents.release();
                }
            };

    @Test
    public void testLoggedOutGoesToLoginActivity() {
        mActivityRule.getActivity().setAuthenticator(MockAuthenticator.getInstance());
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }
}