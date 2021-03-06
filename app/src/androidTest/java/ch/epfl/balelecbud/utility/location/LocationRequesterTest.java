package ch.epfl.balelecbud.utility.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;

import androidx.fragment.app.Fragment;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.view.settings.SettingsFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@SdkSuppress(maxSdkVersion = (Build.VERSION_CODES.Q - 1))
@RunWith(AndroidJUnit4.class)
public class LocationRequesterTest {
    private static final long TIMEOUT = 1000;
    private static String LOCATION_ENABLE_TITLE;
    private static String LOCATION_INFO_TITLE;
    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    MockDatabase.getInstance().resetDatabase();
                    setDumLocationClient();

                }
            };
    private UiDevice device;

    private void grantPermission() {
        if (this.device.hasObject(By.text(LOCATION_INFO_TITLE))) {
            this.device.findObject(By.text(LOCATION_INFO_TITLE)).click();
            this.device.waitForWindowUpdate(null, TIMEOUT);
            this.device.findObject(By.text("ALLOW")).click();
            this.device.waitForWindowUpdate(null, TIMEOUT);
        }
    }

    private void openFragment() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.fragment_main_drawer_settings));
        device.waitForIdle();
    }

    private void setDumLocationClient() {
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {

            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {

            }
        });
    }

    @Before
    public void setUp() {
        this.device = UiDevice.getInstance(getInstrumentation());
        this.device.waitForWindowUpdate(null, TIMEOUT);
        LOCATION_ENABLE_TITLE = mActivityRule.getActivity().getString(R.string.location_enable_title);
        LOCATION_INFO_TITLE = mActivityRule.getActivity().getString(R.string.location_info_title);
        openFragment();
        grantPermission();

        if (LocationUtils.isLocationActive())
            clickOn(LOCATION_ENABLE_TITLE);
    }

    @After
    public void tearDown() {
        setDumLocationClient();
        if (LocationUtils.isLocationActive())
            clickOn(LOCATION_ENABLE_TITLE);
    }

    @Test
    public void testCanSwitchOnLocation() {
        TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                sync.assertNotNull(lr);
                sync.assertNotNull(intent);
                sync.call();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                sync.fail();
            }
        });
        Assert.assertFalse(LocationUtils.isLocationActive());
        clickOn(LOCATION_ENABLE_TITLE);
        Assert.assertTrue(LocationUtils.isLocationActive());
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void testCanSwitchOffLocation() {
        TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                sync.assertNotNull(lr);
                sync.assertNotNull(intent);
                sync.call();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                sync.assertNotNull(intent);
                sync.call();
            }
        });
        clickOn(LOCATION_ENABLE_TITLE);
        clickOn(LOCATION_ENABLE_TITLE);

        Assert.assertFalse(LocationUtils.isLocationActive());
        sync.assertCalled(2);
        sync.assertNoFailedTests();
    }

    private void checkPermissionAfterResult(String[] permissions, int[] permissionStatus, boolean b) {
        Fragment fragment = mActivityRule.getActivity().getSupportFragmentManager()
                .findFragmentByTag(SettingsFragment.TAG);
        fragment.onRequestPermissionsResult(
                LocationUtils.LOCATION_PERMISSIONS_REQUEST_CODE,
                permissions,
                permissionStatus);
        checkDisplayed(b ? LOCATION_ENABLE_TITLE : LOCATION_INFO_TITLE);
    }

    @Test
    public void whenPermissionGrantedCanSwitchOnLocation() {
        checkPermissionAfterResult(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_GRANTED}, true);
    }

    @Test
    public void whenPermissionDeniedCannotSwitchOnLocation() {
        checkPermissionAfterResult(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_DENIED}, false);
    }

    @Test
    public void whenPermissionCanceledCannotSwitchOnLocation() {
        checkPermissionAfterResult(new String[]{}, new int[]{}, false);
    }

    private void clickOn(String title) {
        onView(withId(androidx.preference.R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(title)), click()));
    }

    private void checkDisplayed(String text) {
        onView(new RecyclerViewMatcher(androidx.preference.R.id.recycler_view).
                atPosition(0)).check(matches(hasDescendant(withText(text))));
    }
}
