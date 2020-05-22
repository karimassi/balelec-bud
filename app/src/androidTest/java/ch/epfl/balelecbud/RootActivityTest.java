package ch.epfl.balelecbud;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.view.map.MapViewFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppAuthenticator;
import static ch.epfl.balelecbud.utility.database.MockDatabase.karim;

public class RootActivityTest {
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule = new ActivityTestRule<RootActivity>(RootActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            setAppAuthenticator(MockAuthenticator.getInstance());
            MockAuthenticator.getInstance().setCurrentUser(karim);
        }
    };

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
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void canOpenHomeFragmentFromDrawer() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_home, R.id.activity_home_linear_layout);
    }

    @Test
    public void canOpenInfoFragmentFromDrawer() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_info, R.id.festivalInfoRecyclerView);
    }

    @Test
    public void canOpenScheduleFragmentFromDrawer() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_schedule, R.id.fragment_schedule_linear_layout);
    }

    @Test
    public void canOpenPOIFragmentFromDrawer() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_poi, R.id.pointOfInterestRecyclerView);
    }

    @Test
    public void canOpenPlaylistFragmentFromDrawer() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_playlist, R.id.recycler_view_playlist);
    }

    @Test
    public void canOpenSocialFragmentFomDrawerWhenSignedIn() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_social, R.id.tabs_social);
    }

    @Test
    public void canOpenEmergencyInfoFragmentromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {
        });
        openDrawer();
        clickItem(R.id.fragment_main_drawer_emergency_info, R.id.emergency_info_constraint_layout);
    }

    @Test
    public void canOpenSettingsFragmentFromDrawer() {
        MapViewFragment.setMockCallback(googleMap -> {
        });
        openDrawer();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.fragment_main_drawer_settings));
    }

    @Test
    public void canOpenGalleryActivityFromDrawer(){
        openDrawer();
        clickItem(R.id.fragment_main_drawer_gallery, R.id.gallery_recycler_view);
    }

    @Test
    public void canOpenTransportFragmentFromDrawer() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_transport, R.id.transport_fragment_container);
    }

    @Test
    public void canOpenPicturesFragmentFromDrawer() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_pictures, R.id.fragment_pictures_linear_layout);
    }
}