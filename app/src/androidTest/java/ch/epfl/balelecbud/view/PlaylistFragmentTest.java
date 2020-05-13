package ch.epfl.balelecbud.view;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.view.playlist.PlaylistFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertFalse;

@RunWith(AndroidJUnit4.class)
public class PlaylistFragmentTest {

    @Before
    public void setup() {
        FragmentScenario.launchInContainer(PlaylistFragment.class);
    }

    @Test
    public void linearLayoutIsDisplayed() {
        onView(withId(R.id.fragment_playlist_linear_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void spotifyButtonIsDisplayed() {
        onView(withId(R.id.spotify_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickSpotifyButton() {
        onView(withId(R.id.spotify_button)).perform(click());
        assertFalse(PlaylistFragment.newInstance().isSpotifyConnected());
    }
}