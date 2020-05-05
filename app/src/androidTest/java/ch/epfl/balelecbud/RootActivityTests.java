package ch.epfl.balelecbud;

import android.app.PendingIntent;
import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationRequest;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.map.MapViewFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertFalse;

public class RootActivityTests {
    private static UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule = new ActivityTestRule<>(RootActivity.class);

    @Test
    public void testBackPress() {
        openDrawer();
        device.pressBack();
        device.waitForIdle();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        device.pressBack();
        device.waitForIdle();
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

    @Ignore
    @Test
    public void openMapActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {});
        openDrawer();
        clickItem(R.id.activity_main_drawer_map, R.id.map_view);
    }

    @Test
    public void openEmergencyActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {});
        openDrawer();
        clickItem(R.id.activity_main_drawer_emergency, R.id.fragment_emergency_linear_layout);
    }

    @Test
    public void openEmergencyInfoActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {});
        openDrawer();
        clickItem(R.id.activity_main_drawer_emergency_info, R.id.emergency_info_constraint_layout);
    }

    @Test
    public void openEmergencyNumbersActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {});
        openDrawer();
        clickItem(R.id.activity_main_drawer_emergency_numbers, R.id.emergency_number_layout);
    }

    @Test
    public void openSettingsActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {});
        openDrawer();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_settings));
    }

    @Test
    public void openTransportActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_transport, R.id.transport_fragment_container);
    }

    @Ignore
    @Test
    public void openSocialActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_social, R.id.tabs_social);
    }

    @Ignore
    @Test
    public void signOutDisableLocation() {
        LocationUtil.enableLocation();
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
            }
        });
        openDrawer();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        assertFalse(LocationUtil.isLocationActive());
    }
}