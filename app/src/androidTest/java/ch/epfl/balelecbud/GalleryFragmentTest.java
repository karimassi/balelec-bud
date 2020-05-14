package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.gallery.GalleryFragment;
import ch.epfl.balelecbud.utility.storage.MockStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class GalleryFragmentTest {

    private final MockStorage mockStorage = new MockStorage();

    @Before
    public void setup() {
        mockStorage.setAccessCount(0);
        BalelecbudApplication.setAppStorage(mockStorage);
        FragmentScenario.launchInContainer(GalleryFragment.class);
    }

    @Test
    public void testRecyclerViewVisible() {
        onView(ViewMatchers.withId(R.id.galleryRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testLinearLayoutVisible(){
        onView(ViewMatchers.withId(R.id.fragment_gallery_linear_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testRefreshLayoutVisible(){
        onView(ViewMatchers.withId(R.id.swipe_refresh_layout_gallery)).check(matches(isDisplayed()));
    }

    @Test
    public void testConstraintLayoutVisible() {
        onView(ViewMatchers.withId(R.id.gallery_constraint_layout)).check(matches(isDisplayed()));
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_gallery)).perform(swipeDown());
    }
}
