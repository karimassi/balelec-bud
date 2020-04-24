package ch.epfl.balelecbud;

import android.app.PendingIntent;
import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.map.MapViewFragment;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.alex;
import static org.junit.Assert.assertFalse;

public abstract class RootActivityTest {
    private static UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Before
    public void passIDsToBasicActivityTest() {
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        MockAuthenticator.getInstance().setCurrentUser(alex);
        BalelecbudApplication.setAppDatabaseWrapper(MockDatabaseWrapper.getInstance());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    @Test
    public void testDrawer() {
        openDrawer();
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_info, R.id.festivalInfoRecyclerView);
    }

    @Test
    public void openScheduleActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_schedule, R.id.scheduleRecyclerView);
    }

    @Test
    public void openPOIActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_poi, R.id.pointOfInterestRecyclerView);
    }

    @Test
    public void openMapActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {});
        openDrawer();
        clickItem(R.id.activity_main_drawer_map, R.id.map_view);
    }

    @Test
    public void openTransportActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_transport, R.id.transport_fragment_container);
    }

    @Test
    public void openSocialActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_social, R.id.tabs_social);
    }

    @Test
    public void signOutFromDrawer() {
        openDrawer();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutDisableLocation() {
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) { }

            @Override
            public void removeLocationUpdates(PendingIntent intent) { }
        });
        LocationUtil.enableLocation();
        openDrawer();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        assertFalse(LocationUtil.isLocationActive());
    }

    @Test
    public void testBackPress() {
        openDrawer();
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        device.pressBack();
    }

    public static void openDrawer() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
    }

    public static void clickItem(int itemId, int viewToDisplayId) {
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(itemId));
        device.waitForIdle();
        onView(withId(viewToDisplayId)).check(matches(isDisplayed()));
    }

}
