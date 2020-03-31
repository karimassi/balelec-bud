package ch.epfl.balelecbud.transport;

import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.TransportActivity;
import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.transport.objects.Transport;
import ch.epfl.balelecbud.transport.objects.TransportType;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TransportRecyclerViewTest {

    final Transport transport1 = new Transport(TransportType.BUS, 10, "Nyon", null, Timestamp.now());
    final Transport transport2 = new Transport(TransportType.METRO, 12, "EPFL", null, Timestamp.now());
    final Transport transport3 = new Transport(TransportType.BUS, 122, "La lune", null, Timestamp.now());

    MockDatabaseWrapper mock;

    @Rule
    public final ActivityTestRule<TransportActivity> mActivityRule = new ActivityTestRule<TransportActivity>(TransportActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = new MockDatabaseWrapper();
            MyTransportRecyclerViewAdapter.setDatabaseImplementation(mock);
        }
    };

    private void compareViewAndItem(ViewInteraction viewInt, Transport transport){
        viewInt.check(matches(hasDescendant(withText(transport.getTypeString()))));
        viewInt.check(matches(hasDescendant(withText(transport.getLineString()))));
        viewInt.check(matches(hasDescendant(withText(transport.getDirection()))));
        viewInt.check(matches(hasDescendant(withText(transport.getTimeString()))));
    }

    @Test
    public void displayIsUpdatedWhenItemsAdded() throws Throwable {

        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));

        mock.addItem(transport1);

        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
        onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)).perform(click());

        mock.addItem(transport2);
        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(2)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(1)), transport2);

        mock.addItem(transport3);
        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(3)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(1)), transport2);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(2)), transport3);
    }

    @Test
    public void displayIsUpdatedWhenItemsModified() throws Throwable {

        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));

        mock.addItem(transport1);

        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);

        mock.modifyItem(transport2, 0);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport2);
    }

    @Test
    public void displayIsUpdatedWhenItemsRemoved() throws Throwable {

        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));

        mock.addItem(transport1);

        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);

        mock.removeItem(transport1, 0);

        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));
    }

    @Test
    public void canCreateOtherFragment() {
        TransportListFragment.newInstance();
    }

    @Test
    public void testDrawer() {
        onView(withId(R.id.transport_activity_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.transport_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_info));
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));

    }

    @Test
    public void openScheduleActivityFromDrawer() {
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.transport_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_schedule));
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openPOIActivityFromDrawer() {
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.transport_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_poi));
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openMapActivityFromDrawer() {
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.transport_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_map));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void openTransportActivityFromDrawer() {
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.transport_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_transport));
        onView(withId(R.id.fragmentTransportList)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutFromDrawer() {
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.transport_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
    }

    @Test
    public void testBackPress(){
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.transport_activity_nav_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.transport_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
    }
}
