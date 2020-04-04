package ch.epfl.balelecbud;

import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestData;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PointOfInterestActivityTest {

    MockDatabaseWrapper mock;
    private PointOfInterest pointOfInterest1 = new PointOfInterest(
            new GeoPoint(4, 20), "Bar IC", "Bar", "FUN101");
    private PointOfInterest pointOfInterest2 = new PointOfInterest(
            new GeoPoint(4, 22), "Bar EE", "Bar", "UNFUN101");

    @Before
    public void setup(){
        mock.resetDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH);
        refreshRecyclerView();
    }

    @Rule
    public final ActivityTestRule<PointOfInterestActivity> mActivityRule =
            new ActivityTestRule<PointOfInterestActivity>(PointOfInterestActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
            PointOfInterestData.setDatabaseImplementation(mock);
        }
    };

    @Test
    public void testPointOfInterestRecyclerViewIsDisplayed() {
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddPOIToDatabase() throws Throwable {
        mock.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, pointOfInterest1);

        refreshRecyclerView();

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), pointOfInterest1);
    }

    @Ignore("modifying currently does not make sense")
    @Test
    public void testCanModifyFromDatabase() throws Throwable {
        modifyAndTest(0, true);
    }

    @Ignore("modifying currently does not make sense")
    @Test
    public void testCantModifyFromDataThatIsNotThere() throws Throwable {
        modifyAndTest(2, false);
    }

    private void modifyAndTest(int indexOfMod, boolean pointOfInterestIsModified) throws Throwable {
        PointOfInterest modified = new PointOfInterest(new GeoPoint(6.7, 55),
                "Bar IC", "Bar", "SAD101");

        mock.addItem(pointOfInterest1);
        mock.modifyItem(modified, indexOfMod);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), (pointOfInterestIsModified ? modified : pointOfInterest1));
    }

    @Test
    public void testCanDeleteFromDatabase() throws Throwable {
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

    @Ignore("Not clear what this should do")
    @Test
    public void testCantDeleteFromEmptyDatabase() throws Throwable {
        mock.removeItem(pointOfInterest1, 0);
    }

    private void testInfoInView(ViewInteraction viewInteraction, PointOfInterest poi) {
        viewInteraction.check(matches(hasDescendant(withText(poi.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(poi.getType()))));
        viewInteraction.check(matches(hasDescendant(withText(new Location(poi.getLocation()).toString()))));
    }

    @Test
    public void testDrawer() {
        onView(withId(R.id.poi_activity_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.poi_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_info));
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));

    }

    @Test
    public void openScheduleActivityFromDrawer() {
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.poi_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_schedule));
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openPOIActivityFromDrawer() {
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.poi_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_poi));
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openMapActivityFromDrawer() {
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.poi_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_map));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void openTransportActivityFromDrawer() {
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.poi_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_transport));
        onView(withId(R.id.fragmentTransportList)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutFromDrawer() {
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.poi_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackPress(){
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.poi_activity_nav_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.poi_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
    }
}