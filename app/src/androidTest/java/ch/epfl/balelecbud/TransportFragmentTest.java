package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.transport.objects.TransportStation;
import ch.epfl.balelecbud.util.http.MockHttpClient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TransportFragmentTest {

    private TransportStation lausanne = new TransportStation(Location.DEFAULT_LOCATION, "0", "Lausanne", 100);

    @Before
    public void setup() {
        BalelecbudApplication.setHttpClient(MockHttpClient.getInstance());
        FragmentScenario.launchInContainer(TransportFragment.class);
    }

    private void compareViewAndItem(ViewInteraction viewInt, TransportStation transport){
        viewInt.check(matches(hasDescendant(withText(transport.getStationName()))));
        viewInt.check(matches(hasDescendant(withText(String.valueOf(transport.getDistanceToUser())))));
    }

    @Test
    public void testStationIsDisplayed() {
        onView(withId(R.id.recycler_view_transport_stations)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.recycler_view_transport_stations).atPosition(0)), lausanne);
    }

    @Test
    public void testConnectionsDisplayed() {
        onView(withId(R.id.recycler_view_transport_stations)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recycler_view_transport_departures)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view_transport_departures)).check(matches(hasChildCount(1)));
    }

    @Test
    public void testPressBackReturnsToStations() {
        onView(withId(R.id.recycler_view_transport_stations)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recycler_view_transport_departures)).check(matches(isDisplayed()));
        onView(isRoot()).perform(pressBack());
        onView(withId(R.id.recycler_view_transport_stations)).check(matches(isDisplayed()));
    }
}
