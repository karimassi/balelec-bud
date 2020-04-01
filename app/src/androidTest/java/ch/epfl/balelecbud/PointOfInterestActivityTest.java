package ch.epfl.balelecbud;

import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestAdapter;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
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
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
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
    public void testCanModifyFromDatabase() throws Throwable {
        modifyAndTest(0, true);
    }

    @Test
    public void testCantModifyFromDataThatIsNotThere() throws Throwable {
        modifyAndTest(2, false);
    }

    private void modifyAndTest(int indexOfMod, boolean pointOfInterestIsModified) throws Throwable {
        PointOfInterest modified = new PointOfInterest(new GeoPoint(6.7, 55),
                "Bar IC", "Bar", "SAD101");

        mock.addItem(pointOfInterest);
        mock.modifyItem(modified, indexOfMod);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), (pointOfInterestIsModified ? modified : pointOfInterest));
    }

    @Test
    public void testCanDeleteFromDatabase() throws Throwable {
        mock.addItem(pointOfInterest);
        mock.addItem(pointOfInterest);
        mock.removeItem(pointOfInterest, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest);
    }

    @Test
    public void testCantDeleteFromEmptyDatabase() throws Throwable {
        mock.removeItem(pointOfInterest, 0);
    }

    private void testInfoInView(ViewInteraction viewInteraction, PointOfInterest poi) {
        viewInteraction.check(matches(hasDescendant(withText(poi.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(poi.getType()))));
        viewInteraction.check(matches(hasDescendant(withText(new Location(poi.getLocation()).toString()))));
    }

    /**@Test
    public void testDrawer() {
        onView(withId(R.id.root_activity_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_info));
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));

    }

    @Test
    public void openScheduleActivityFromDrawer() {
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_schedule));
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openPOIActivityFromDrawer() {
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_poi));
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openMapActivityFromDrawer() {
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_map));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void openTransportActivityFromDrawer() {
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_transport));
        onView(withId(R.id.fragmentTransportList)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutFromDrawer() {
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackPress(){
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
    }**/
}