package ch.epfl.balelecbud.view.pointOfInterest;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.view.map.MapViewFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickChildViewWithId;
import static ch.epfl.balelecbud.utility.database.Database.POINT_OF_INTEREST_PATH;
import static ch.epfl.balelecbud.utility.database.MockDatabase.alex;
import static ch.epfl.balelecbud.utility.database.MockDatabase.pointOfInterest1;
import static ch.epfl.balelecbud.utility.database.MockDatabase.pointOfInterest2;

@RunWith(AndroidJUnit4.class)
public class PointOfInterestToMapButtonTest {
    @Rule
    public ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    MockAuthenticator mockAuth = MockAuthenticator.getInstance();
                    MockDatabase mockDB = MockDatabase.getInstance();
                    mockDB.resetDatabase();
                    BalelecbudApplication.setAppDatabase(mockDB);
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    mockAuth.setCurrentUser(alex);
                    mockDB.resetDocument(POINT_OF_INTEREST_PATH);
                    mockDB.storeDocument(POINT_OF_INTEREST_PATH, pointOfInterest1);
                    mockDB.storeDocument(POINT_OF_INTEREST_PATH, pointOfInterest2);
                    MapViewFragment.setMockCallback(mapboxMap -> {
                    });
                }
            };

    @Before
    public void openPOI() {
        onView(withId(R.id.root_activity_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.fragment_main_drawer_poi));
    }

    private void clickOnButtonAndCheckMapOpen(int i) {
        onView(withId(R.id.pointOfInterestRecyclerView)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, clickChildViewWithId(R.id.go_to_map)));
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void opensTheMapWhenClickOnAPOI1Button() {
        clickOnButtonAndCheckMapOpen(0);
    }

    @Test
    public void opensTheMapWhenClickOnAPOI2Button() {
        clickOnButtonAndCheckMapOpen(1);
    }

}
