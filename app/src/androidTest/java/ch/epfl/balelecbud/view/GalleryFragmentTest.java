package ch.epfl.balelecbud.view;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.storage.MockStorage;
import ch.epfl.balelecbud.view.gallery.GalleryFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.nthChildOf;

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
        onView(ViewMatchers.withId(R.id.gallery_recycler_view)).check(matches(isDisplayed()));
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

    @Test
    public void testImagesAreRetrievedFromMockStorage(){
        refreshRecyclerView();
        onView(ViewMatchers.withId(R.id.gallery_recycler_view)).check(matches(hasChildCount(9)));
    }

    @Test
    public void testImagesAreBindedToView(){
        refreshRecyclerView();
        for(int i = 0 ; i < 9 ; ++i){
            onView(nthChildOf(withId(R.id.gallery_recycler_view), i)).check(matches(isDisplayed()));
        }
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_gallery)).perform(swipeDown());
    }
}
