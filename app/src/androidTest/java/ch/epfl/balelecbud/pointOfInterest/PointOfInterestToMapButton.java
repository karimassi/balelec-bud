package ch.epfl.balelecbud.pointOfInterest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.PointOfInterestActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.map.MapViewActivity;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.models.Location.DEFAULT_LOCATION;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.nthChildOf;
import static ch.epfl.balelecbud.util.database.DatabaseWrapper.POINT_OF_INTEREST_PATH;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.pointOfInterest1;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.pointOfInterest2;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class PointOfInterestToMapButton {
    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();
    private final Location defaultLocation = DEFAULT_LOCATION;
    @Rule
    public ActivityTestRule<PointOfInterestActivity> mActivityRule =
            new ActivityTestRule<PointOfInterestActivity>(PointOfInterestActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppDatabaseWrapper(mock);
                    mock.resetDocument(POINT_OF_INTEREST_PATH);
                    mock.storeDocument(POINT_OF_INTEREST_PATH, pointOfInterest1);
                    mock.storeDocument(POINT_OF_INTEREST_PATH, pointOfInterest2);
                    MapViewActivity.setMockCallback(mapboxMap -> {});
                }
            };

    @Before
    @After
    public void resetDefaultLocation() {
        DEFAULT_LOCATION = defaultLocation;
    }

    @Test
    public void opensTheMapWhenClickOnAPOI1Button() {
        onView(nthChildOf(nthChildOf(withId(R.id.pointOfInterestRecyclerView), 0), 5)).perform(click());
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void opensTheMapWhenClickOnAPOI2Button() {
        onView(nthChildOf(nthChildOf(withId(R.id.pointOfInterestRecyclerView), 1), 5)).perform(click());
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void defaultLocationSetCorrectlyForPOI1() {
        onView(nthChildOf(nthChildOf(withId(R.id.pointOfInterestRecyclerView), 0), 5)).perform(click());
        assertThat(DEFAULT_LOCATION, is(new Location(pointOfInterest1.getLocation())));
    }

    @Test
    public void defaultLocationSetCorrectlyForPOI2() {
        onView(nthChildOf(nthChildOf(withId(R.id.pointOfInterestRecyclerView), 1), 5)).perform(click());
        assertThat(DEFAULT_LOCATION, is(new Location(pointOfInterest2.getLocation())));
    }
}
