package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;
import ch.epfl.balelecbud.models.PointOfInterestAdapter;
import ch.epfl.balelecbud.models.PointOfInterest;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PointOfInterestActivityTest {

    MockDatabaseWrapper mock;
    private PointOfInterest pointOfInterest = new PointOfInterest(
            new GeoPoint(4, 20), "Bar IC", "Bar", "FUN101");

    @Rule
    public final ActivityTestRule<PointOfInterestActivity> mActivityRule =
            new ActivityTestRule<PointOfInterestActivity>(PointOfInterestActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = new MockDatabaseWrapper();
            PointOfInterestAdapter.setDatabaseImplementation(mock);
        }
    };

    @Test
    public void testPointOfInterestRecyclerViewIsDisplayed() {
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddPOIToDatabase() throws Throwable {
        mock.addItem(pointOfInterest);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest);
    }

    @Test
    public void testCanModifyPOIFromDatabase() throws Throwable {
        PointOfInterest modified = new PointOfInterest(new GeoPoint(6.7, 55),
                "Bar IC", "Bar", "GOOD101");

        mock.addItem(pointOfInterest);
        mock.modifyItem(modified, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), modified);
    }

    @Test
    public void testCantModifyPOIFromDatabaseThatIsNotThere() throws Throwable {
        PointOfInterest modified = new PointOfInterest(new GeoPoint(6.7, 55),
                "Bar IC", "Bar", "BAD101");

        mock.addItem(pointOfInterest);
        mock.modifyItem(modified, 2);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest);
    }

    @Test
    public void testCanDeletePOIFromDatabase() throws Throwable {
        mock.addItem(pointOfInterest);
        mock.addItem(pointOfInterest);
        mock.removeItem(pointOfInterest, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest);
    }

    @Test
    public void testCantDeletePOIFromEmptyDatabase() throws Throwable {
        mock.removeItem(pointOfInterest, 0);
    }

    private void testInfoInView(ViewInteraction viewInteraction, PointOfInterest poi) {
        viewInteraction.check(matches(hasDescendant(withText(poi.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(poi.getType()))));
        viewInteraction.check(matches(hasDescendant(withText(poi.getLocation().toString()))));
    }
}