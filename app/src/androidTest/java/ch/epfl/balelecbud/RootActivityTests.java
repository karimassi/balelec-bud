package ch.epfl.balelecbud;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.view.map.MapViewFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class RootActivityTests {
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule = new ActivityTestRule<>(RootActivity.class);

    private void openDrawer() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
    }

    private void clickItem(int itemId, int viewToDisplayId) {
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(itemId));
        device.waitForIdle();
        onView(withId(viewToDisplayId)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackPress() {
        openDrawer();
        device.pressBack();
        device.waitForIdle();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        device.pressBack();
        device.waitForIdle();
    }

    @Test
    public void testDrawer() {
        openDrawer();
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void canOpenInfoActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_info, R.id.festivalInfoRecyclerView);
    }

    @Test
    public void canOpenScheduleActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_schedule, R.id.scheduleRecyclerView);
    }

    @Test
    public void canOpenPOIActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_poi, R.id.pointOfInterestRecyclerView);
    }

    @Test
    public void canOpenPlaylistActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_playlist, R.id.recycler_view_playlist);
    }

    @Test
    public void canOpenEmergencyInfoActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {
        });
        openDrawer();
        clickItem(R.id.activity_main_drawer_emergency_info, R.id.emergency_info_constraint_layout);
    }

    @Test
    public void canOpenSettingsActivityFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {
        });
        openDrawer();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_settings));
    }

    @Test
    public void canOpenTransportActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_transport, R.id.transport_fragment_container);
    }
}