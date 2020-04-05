package ch.epfl.balelecbud.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Switch;

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

import ch.epfl.balelecbud.MapViewActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.testUtils.CustomViewAssertion.switchClickable;

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N_MR1, maxSdkVersion = (Build.VERSION_CODES.Q - 1))
@RunWith(AndroidJUnit4.class)
public class LocationRequesterTest {
    private static final long TIMEOUT = 1000;
    private UiDevice device;

    private void grantPermission() {
        if (this.device.hasObject(By.text("ALLOW"))) {
            this.device.findObject(By.text("ALLOW")).click();
            this.device.waitForWindowUpdate(null, TIMEOUT);
        }
    }

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    setDumLocationClient();
                }
            };

    private void setDumLocationClient() {
        LocationUtil.setLocationClient(new LocationClient() {
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
        grantPermission();
        if (LocationUtil.isLocationActive())
            onView(withId(R.id.locationSwitch)).perform(click());
    }

    @After
    public void tearDown() {
        setDumLocationClient();
        if (LocationUtil.isLocationActive())
            onView(withId(R.id.locationSwitch)).perform(click());
    }

    @Test
    public void testCanSwitchOnLocation() {
        TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtil.setLocationClient(new LocationClient() {
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
        Assert.assertFalse(LocationUtil.isLocationActive());
        onView(withId(R.id.locationSwitch)).check(switchClickable(true));
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertTrue(LocationUtil.isLocationActive());
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void testCanSwitchOffLocation() {
        TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtil.setLocationClient(new LocationClient() {
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
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertFalse(LocationUtil.isLocationActive());
        sync.assertCalled(2);
        sync.assertNoFailedTests();
    }

    private void checkPermissionAfterResult(String[] permissions, int[] permissionStatus, boolean b) {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE,
                permissions,
                permissionStatus);
        onView(withId(R.id.locationSwitch)).check(switchClickable(b));
    }

    @Test
    public void whenPermissionGrantedCanSwitchOnLocation() {
        checkPermissionAfterResult(
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                new int[] { PackageManager.PERMISSION_GRANTED}, true);
    }

    @Test
    public void whenPermissionDeniedCannotSwitchOnLocation() {
        checkPermissionAfterResult(
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                new int[] { PackageManager.PERMISSION_DENIED }, false);
    }

    @Test
    public void whenPermissionCanceledCannotSwitchOnLocation() {
        checkPermissionAfterResult(new String[] { }, new int[] { }, false);
    }

    @Test
    public void whenPermissionIsForAnOtherActivitySwitchStateNotChanged() {
        Switch locationSwitch = this.mActivityRule.getActivity().findViewById(R.id.locationSwitch);
        boolean before = locationSwitch.isClickable();

        this.mActivityRule.getActivity().onRequestPermissionsResult(0,
                new String[] { }, new int[] { });

        onView(withId(R.id.locationSwitch)).check(switchClickable(before));
    }

    @Test
    public void testSwitchOffDisablesLocationInMap() {
        TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                sync.fail();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                sync.fail();
            }
        });
        onView(withId(R.id.mapButton)).perform(click());
        Assert.assertFalse(MapViewActivity.getLocationPermission());
        sync.assertCalled(0);
        sync.assertNoFailedTests();
    }
}
