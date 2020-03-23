package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestAdapter;
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
    private String pointOfInterest = "ATM";

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
        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).atPosition(0)), pointOfInterest);
    }

    @Test
    public void testCanModifyPOIFromDatabase() throws Throwable {
        String modified = "Bar IC";

        mock.addItem(pointOfInterest);
        mock.modifyItem(modified, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).atPosition(0)), modified);
    }

    @Test
    public void testCantModifyPOIFromDatabaseThatIsNotThere() throws Throwable {
        String modified = "Bar";

        mock.addItem(pointOfInterest);
        mock.modifyItem(modified, 2);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).atPosition(0)), pointOfInterest);
    }

    @Test
    public void testCanDeletePOIFromDatabase() throws Throwable {
        mock.addItem(pointOfInterest);
        mock.addItem(pointOfInterest);
        mock.removeItem(pointOfInterest, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).atPosition(0)), pointOfInterest);
    }

    @Test
    public void testCantDeletePOIFromEmptyDatabase() throws Throwable {
        mock.removeItem("", 0);
    }

    private void testInfoInView(ViewInteraction viewInteraction, String element) {
        viewInteraction.check(matches(hasDescendant(withText(element)))); //name
        viewInteraction.check(matches(hasDescendant(withText(element)))); //type
        viewInteraction.check(matches(hasDescendant(withText(element)))); //geoPoint
    }
}