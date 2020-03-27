package ch.epfl.balelecbud.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.Switch;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;

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
        if (LocationUtil.isLocationActive(this.mActivityRule.getActivity()))
            onView(withId(R.id.locationSwitch)).perform(click());
    }

    @After
    public void tearDown() {
        setDumLocationClient();
        if (LocationUtil.isLocationActive(this.mActivityRule.getActivity()))
            onView(withId(R.id.locationSwitch)).perform(click());
    }

    @Test
    public void testCanSwitchOnLocation() {
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                Assert.assertNotNull(lr);
                Assert.assertNotNull(intent);
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                Assert.fail();
            }
        });
        Assert.assertFalse(LocationUtil.isLocationActive(mActivityRule.getActivity()));
        onView(withId(R.id.locationSwitch)).check(switchClickable(true));
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertTrue(LocationUtil.isLocationActive(mActivityRule.getActivity()));
    }

    @Test
    public void testCanSwitchOffLocation() {
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                Assert.assertNotNull(lr);
                Assert.assertNotNull(intent);
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                Assert.assertNotNull(intent);
            }
        });
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertFalse(LocationUtil.isLocationActive(this.mActivityRule.getActivity()));
    }

    @Test
    public void whenPermissionGrantedCanSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_GRANTED});
        onView(withId(R.id.locationSwitch)).check(switchClickable(true));
    }

    @Test
    public void whenPermissionDeniedCannotSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_DENIED});
        onView(withId(R.id.locationSwitch)).check(switchClickable(false));
    }

    @Test
    public void whenPermissionCanceledCannotSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE,
                new String[]{},
                new int[]{});
        onView(withId(R.id.locationSwitch)).check(switchClickable(false));
    }

    @Test
    public void whenPermissionIsForAnOtherActivitySwitchStateNotChanged() {
        Switch locationSwitch = this.mActivityRule.getActivity().findViewById(R.id.locationSwitch);
        boolean before = locationSwitch.isClickable();

        this.mActivityRule.getActivity().onRequestPermissionsResult(0,
                new String[]{}, new int[]{});

        Assert.assertEquals(locationSwitch.isClickable(), before);
    }

    @Test
    public void testSwitchOnEnablesLocationInMap() {
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                Assert.assertNotNull(lr);
                Assert.assertNotNull(intent);
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                Assert.fail();
            }
        });
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.mapButton)).perform(click());
        Assert.assertTrue(MapViewActivity.getLocationPermission());
    }

    @Test
    public void testSwitchOffDisablesLocationInMap() {
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                Assert.assertNotNull(lr);
                Assert.assertNotNull(intent);
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                Assert.assertNotNull(intent);
            }
        });
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.mapButton)).perform(click());
        Assert.assertFalse(MapViewActivity.getLocationPermission());
    }

    public static ViewAssertion switchClickable(final boolean isClickable) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null)
                    throw noViewFoundException;
                if (!(view instanceof Switch))
                    throw new AssertionError("The View should be a Switch be was not");
                Assert.assertThat(((Switch) view).isClickable(), is(isClickable));
            }
        };
    }
}
