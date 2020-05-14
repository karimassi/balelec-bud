package ch.epfl.balelecbud.view;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Track;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.view.playlist.PlaylistFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.utility.database.Database.PLAYLIST_PATH;
import static junit.framework.TestCase.assertFalse;

@RunWith(AndroidJUnit4.class)
public class PlaylistFragmentTest {

    private final MockDatabase mock = MockDatabase.getInstance();
    private final Track track = new Track("i'm cool.", "C-line ft. K-rim", "uri", 0);

    @Before
    public void setup() {
        mock.resetDatabase();
        BalelecbudApplication.setAppDatabase(mock);
        mock.storeDocument(PLAYLIST_PATH, track);
        FragmentScenario.launchInContainer(PlaylistFragment.class);
    }

    @After
    public void cleanUp() {
        mock.resetDocument(PLAYLIST_PATH);
    }

    @Test
    public void testPlaylistRecyclerViewIsDisplayed() {
        onView(ViewMatchers.withId(R.id.recycler_view_playlist)).check(matches(isDisplayed()));
    }

    @Test
    public void testTrackIsDisplayed() {
        onView(withId(R.id.swipe_refresh_layout_playlist)).perform(swipeDown());
        testInfoInView(onView(new RecyclerViewMatcher(R.id.recycler_view_playlist).atPosition(0)), track);
    }

    @Test
    public void testClickOnItemConnectsSpotify() {
        onView(withId(R.id.swipe_refresh_layout_playlist)).perform(swipeDown());
        onView(new RecyclerViewMatcher(R.id.recycler_view_playlist).atPosition(0)).perform(click());
        assertFalse(PlaylistFragment.newInstance().isSpotifyConnected());
    }

    private void testInfoInView(ViewInteraction viewInteraction, Track track) {
        viewInteraction.check(matches(hasDescendant(withText(track.getTitle()))));
        viewInteraction.check(matches(hasDescendant(withText(track.getArtist()))));
    }
}