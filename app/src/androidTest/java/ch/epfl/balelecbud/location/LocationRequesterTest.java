package ch.epfl.balelecbud.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
            new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void setUp() {
        WelcomeActivity.mockMode = true;
        this.device = UiDevice.getInstance(getInstrumentation());
        this.device.waitForWindowUpdate(null, TIMEOUT);
        grantPermission();
    }

    @Test
    public void testCanSwitchOnLocation() {
        this.mActivityRule.getActivity().setLocationClient(new LocationClient() {
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
        Assert.assertTrue(this.mActivityRule.getActivity().isLocationSwitchClickable());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertTrue(mActivityRule.getActivity().isLocationActive());
    }

    @Test
    public void testCanSwitchOffLocation() {
        this.mActivityRule.getActivity().setLocationClient(new LocationClient() {
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
        Assert.assertFalse(mActivityRule.getActivity().isLocationActive());
    }

    @Test
    public void whenPermissionGrantedCanSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                WelcomeActivity.LOCATION_PERMISSIONS_REQUEST_CODE,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_GRANTED});
        Assert.assertTrue(this.mActivityRule.getActivity().isLocationSwitchClickable());
    }

    @Test
    public void whenPermissionDeniedCannotSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                WelcomeActivity.LOCATION_PERMISSIONS_REQUEST_CODE,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_DENIED});
        Assert.assertFalse(this.mActivityRule.getActivity().isLocationSwitchClickable());
    }

    @Test
    public void whenPermissionCanceledCannotSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                WelcomeActivity.LOCATION_PERMISSIONS_REQUEST_CODE,
                new String[]{},
                new int[]{});
        Assert.assertFalse(this.mActivityRule.getActivity().isLocationSwitchClickable());
    }

    @Test
    public void whenPermissionIsForAnOtherActivitySwitchStateNotChanged() {
        boolean before = this.mActivityRule.getActivity().isLocationSwitchClickable();

        this.mActivityRule.getActivity().onRequestPermissionsResult(0,
                new String[]{}, new int[]{});
        Assert.assertThat(this.mActivityRule.getActivity().isLocationSwitchClickable(), is(before));
    }

}
