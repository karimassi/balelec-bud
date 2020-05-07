package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class WelcomeFragmentTest {

    @Before
    public void setup() {
        FragmentScenario.launchInContainer(WelcomeFragment.class);
    }

    @Test
    public void backgroundIsDisplayed() {
        onView(withId(R.id.activity_home_linear_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void spotifyButtonIsDisplayed() {
        onView(withId(R.id.spotify_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickSpotifyButton() {
        onView(withId(R.id.spotify_button)).perform(click());
        assertNull(WelcomeFragment.newInstance().getSpotifyAppRemote());
    }
}