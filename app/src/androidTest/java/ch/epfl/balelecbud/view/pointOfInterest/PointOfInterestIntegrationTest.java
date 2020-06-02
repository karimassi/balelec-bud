package ch.epfl.balelecbud.view.pointOfInterest;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.PointOfInterest;
import ch.epfl.balelecbud.model.PointOfInterestType;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;

import ch.epfl.balelecbud.utility.cache.FilesystemCache;
import ch.epfl.balelecbud.utility.connectivity.AndroidConnectivityChecker;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;



public class PointOfInterestIntegrationTest {

    private final MockDatabase mock = MockDatabase.getInstance();
    private UiDevice device;

    private PointOfInterest p = new PointOfInterest("whatever", PointOfInterestType.BAR,
            new Location(46.51808,6.56906), 0.003);

    private ArrayList<Location> locations = Lists.newArrayList(
            new Location(46.51808, 6.56905),
            new Location(46.51809, 6.56909),
            new Location(4651810, 6.56919),
            new Location(33, 3));

    @Before
    public void setup() {
        mock.resetDatabase();
        BalelecbudApplication.setAppCache(FilesystemCache.getInstance());
        BalelecbudApplication.setRemoteDatabase(mock);
        BalelecbudApplication.setConnectivityChecker(() -> true);

        BalelecbudApplication.getAppCache().flush();

        device = UiDevice.getInstance(getInstrumentation());

        int index = 0;
        for (Location loc : locations) {
            mock.storeDocumentWithID(Database.LOCATIONS_PATH, Integer.toString(index++), loc);
        }

        mock.storeDocument(Database.POINT_OF_INTEREST_PATH, p);
        FragmentScenario.launchInContainer(PointOfInterestFragment.class);
        refreshRecyclerView();
    }

    @After
    public void teardown(){
        BalelecbudApplication.getAppCache().flush();
        BalelecbudApplication.setConnectivityChecker(AndroidConnectivityChecker.getInstance());
    }

    @Test
    public void fetchRemoteAndThenCache() throws InterruptedException {
        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), p, "2");

        //now we use the cache, information should not change
        BalelecbudApplication.setConnectivityChecker(() -> false);
        // resetting the database is not necessary because it currently is not accessed at all
        // but useful safeguard against quick refactoring
        mock.resetDatabase();
        refreshRecyclerView();
        device.wait(Until.findObject(By.text("Last refreshed")), 10_000);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.pointOfInterestRecyclerView).
                atPosition(0)), p, "2");
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_point_of_interest)).perform(swipeDown());
    }

    private void testInfoInView(ViewInteraction viewInteraction, PointOfInterest poi, String expectedAffluence) {
        viewInteraction.check(matches(hasDescendant(withText(poi.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(String.valueOf(expectedAffluence)))));
    }


}
