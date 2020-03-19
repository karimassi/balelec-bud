package ch.epfl.balelecbud.transport;

import androidx.test.espresso.ViewInteraction;
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
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
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
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
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

}
