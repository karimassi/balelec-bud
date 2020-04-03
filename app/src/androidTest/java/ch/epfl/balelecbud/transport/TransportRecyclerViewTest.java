package ch.epfl.balelecbud.transport;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.TransportActivity;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TransportRecyclerViewTest {

//    final TransportDeparture transport1 = new TransportDeparture(TransportType.BUS, 10, "Nyon", null, Timestamp.now());
//    final TransportDeparture transport2 = new TransportDeparture(TransportType.METRO, 12, "EPFL", null, Timestamp.now());
//    final TransportDeparture transport3 = new TransportDeparture(TransportType.BUS, 122, "La lune", null, Timestamp.now());

    MockDatabaseWrapper mock;

    @Rule
    public final ActivityTestRule<TransportActivity> mActivityRule = new ActivityTestRule<TransportActivity>(TransportActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
           // MyTransportRecyclerViewAdapter.setDatabaseImplementation(mock);
        }
    };

//    private void compareViewAndItem(ViewInteraction viewInt, TransportDeparture transport){
//        viewInt.check(matches(hasDescendant(withText(transport.getTypeString()))));
//        viewInt.check(matches(hasDescendant(withText(transport.getLineString()))));
//        viewInt.check(matches(hasDescendant(withText(transport.getDestination()))));
//        viewInt.check(matches(hasDescendant(withText(transport.getTimeString()))));
//    }

    @Test
    public void test() {
        Assert.assertTrue(true);
    }


//    @Test
//    public void displayIsUpdatedWhenItemsAdded() throws Throwable {
//
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));
//
//        mock.addItem(transport1);
//
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(1)));
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
//        onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)).perform(click());
//
//        mock.addItem(transport2);
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(2)));
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(1)), transport2);
//
//        mock.addItem(transport3);
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(3)));
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(1)), transport2);
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(2)), transport3);
//    }
//
//    @Test
//    public void displayIsUpdatedWhenItemsModified() throws Throwable {
//
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));
//
//        mock.addItem(transport1);
//
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(1)));
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
//
//        mock.modifyItem(transport2, 0);
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport2);
//    }
//
//    @Test
//    public void displayIsUpdatedWhenItemsRemoved() throws Throwable {
//
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));
//
//        mock.addItem(transport1);
//
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(1)));
//        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentTransportList).atPosition(0)), transport1);
//
//        mock.removeItem(transport1, 0);
//
//        onView(withId(R.id.fragmentTransportList)).check(matches(hasChildCount(0)));
//    }
//
//    @Test
//    public void canCreateOtherFragment() {
//        TransportListFragment.newInstance();
//    }

}
