package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.pointOfInterest1;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.pointOfInterest2;

@RunWith(AndroidJUnit4.class)
public class PointOfInterestFragmentTest extends RootActivityTest {

    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();
    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    BalelecbudApplication.setAppDatabaseWrapper(mock);
                }
            };

    @Override
    protected void setUpBeforeActivityLaunched() {
        super.setUpBeforeActivityLaunched();
        cleanUp();
    }

    @After
    public void cleanUp() {
        mock.resetDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH);
    }

    @Test
    public void testPointOfInterestRecyclerViewIsDisplayed() {
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddPOIToDatabase() {
        mock.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, pointOfInterest1);

        refreshRecyclerView();

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest1);
    }


    @Test
    public void testCanDeleteFromDatabase() {
        mock.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, pointOfInterest1);
        mock.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, pointOfInterest2);

        refreshRecyclerView();

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest1);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(1)), pointOfInterest2);

        mock.deleteDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, pointOfInterest2);

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
    protected int getItemId() {
        return R.id.activity_main_drawer_poi;
    }

    @Override
    protected int getViewToDisplayId() {
        return R.id.pointOfInterestRecyclerView;
    }
}