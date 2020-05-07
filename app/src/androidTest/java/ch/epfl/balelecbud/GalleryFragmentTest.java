package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.gallery.GalleryFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.nthChildOf;

@RunWith(AndroidJUnit4.class)
public class GalleryFragmentTest {

    @Before
    public void setup() {
        FragmentScenario.launchInContainer(GalleryFragment.class);
    }

    @Test
    public void testLayoutIsDisplayed() {
        onView(withId(R.id.gallery_relative_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.gallery_grid_view)).check(matches(isDisplayed()));
    }

    @Test
    public void imagesAreDisplayed() {
        onView(withId(R.id.gallery_grid_view)).check(matches(hasChildCount(9)));
        imageIsDisplayed(0);
        imageIsDisplayed(1);
        imageIsDisplayed(2);
        imageIsDisplayed(3);
        imageIsDisplayed(4);
        imageIsDisplayed(5);
        imageIsDisplayed(6);
        imageIsDisplayed(7);
        imageIsDisplayed(8);
    }

    private void imageIsDisplayed(int childPosition){
        onView(nthChildOf(withId(R.id.gallery_grid_view), childPosition)).check(matches(isDisplayed()));
    }
}
