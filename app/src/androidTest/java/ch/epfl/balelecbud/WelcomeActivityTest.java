package ch.epfl.balelecbud;

import android.app.PendingIntent;
import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
<<<<<<< HEAD
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
=======
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
>>>>>>> master
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {
    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    LocationUtil.setLocationClient(new LocationClient() {
                        @Override
                        public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {

                        }

                        @Override
                        public void removeLocationUpdates(PendingIntent intent) {

                        }
                    });
                }
            };

    @Before
    public void setup() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    @Test
<<<<<<< HEAD
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
=======
    public void testMapButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.mapButton)));
    }

    @Test
    public void testInfoButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.infoButton)));
    }

    @Test
    public void testTransportButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.transportButton)));
    }

    @Test
    public void testScheduleButtonIsDisplayed() {
        onView(withId(R.id.scheduleButton)).check(matches(isDisplayed()));
        onView(withId(R.id.scheduleButton)).perform(click());
    }

    @Test
    public void testPOIButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.poiButton)));
    }

    @Test
    public void testSignOutIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.buttonSignOut)));
>>>>>>> master
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

<<<<<<< HEAD
    @Test
    public void testBackPress(){
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
=======
    private void testButtonIsDisplayed(ViewInteraction button) {
        button.check(matches(isDisplayed()));
        button.perform(click());
    }

    private void testFeatureIsDisplayed(ViewInteraction button, ViewInteraction feature) {
        button.perform(click());
        feature.check(matches(isDisplayed()));
        button.check(doesNotExist());
>>>>>>> master
    }
}
