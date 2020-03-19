package ch.epfl.balelecbud;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

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
public class MainActivityTest extends BasicAuthenticationTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
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


    @Before
    public void setUp() throws Throwable {
        mActivityRule.getActivity().setAuthenticator(MockAuthenticator.getInstance());
    }

    @Before

    @Test
    public void testLoggedOutGoesToLoginActivity() {
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }
}