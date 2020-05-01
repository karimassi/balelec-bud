package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.util.database.MockDatabase.pointOfInterest1;

@RunWith(AndroidJUnit4.class)
public class PointOfInterestActivityTest extends BasicActivityTest {

    private final MockDatabase mock = MockDatabase.getInstance();
    @Rule
    public final ActivityTestRule<PointOfInterestActivity> mActivityRule =
            new ActivityTestRule<PointOfInterestActivity>(PointOfInterestActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    BalelecbudApplication.setAppDatabase(mock);
                }
            };

    @Before
    public void setup() {
        mock.resetDocument(Database.POINT_OF_INTEREST_PATH);
        refreshRecyclerView();
    }

    @Test
    public void testPointOfInterestRecyclerViewIsDisplayed() {
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddPOIToDatabase() {
        mock.storeDocument(Database.POINT_OF_INTEREST_PATH, pointOfInterest1);

        refreshRecyclerView();

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest1);
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_point_of_interest)).perform(swipeDown());
    }

    private void testInfoInView(ViewInteraction viewInteraction, PointOfInterest poi) {
        viewInteraction.check(matches(hasDescendant(withText(poi.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(poi.getType().toString()))));
    }

    @Override
    protected void setIds() {
        setIds(R.id.poi_activity_drawer_layout, R.id.poi_activity_nav_view);
    }
}