package ch.epfl.balelecbud.location;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.WelcomeActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;

@SdkSuppress(maxSdkVersion = Build.VERSION_CODES.N_MR1 - 1)
@RunWith(AndroidJUnit4.class)
public class LocationRequesterTest22 {
    private static final long TIMEOUT = 1000;
    private FusedLocationProviderClient client;
    private UiDevice device;

//    private void grantPermission() throws IOException {
//        UiDevice.getInstance(getInstrumentation()).executeShellCommand("pm reset-permissions");
//        if (!this.device.hasObject(By.text("ALLOW"))) {
//            this.device.findObject(By.text("ALLOW")).click();
//            this.device.waitForWindowUpdate(null, TIMEOUT);
//        }
//    }

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void setUp() throws IOException {
        this.client = LocationServices.getFusedLocationProviderClient(this.mActivityRule.getActivity());
        this.client.setMockMode(true);
        this.device = UiDevice.getInstance(getInstrumentation());
        this.device.waitForWindowUpdate(null, TIMEOUT);
//        grantPermission();
    }

    @Test
    public void testCanSwitchOnLocation() {
        Assert.assertTrue(this.mActivityRule.getActivity().isLocationSwitchClickable());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertTrue(mActivityRule.getActivity().isLocationActive());
    }

    @Test
    public void testCanSwitchOffLocation() {
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertFalse(mActivityRule.getActivity().isLocationActive());
    }

    @Test
    public void whenPermissionGrantedCanSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                WelcomeActivity.REQUEST_PERMISSIONS_REQUEST_CODE,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                new int[] { PackageManager.PERMISSION_GRANTED });
        Assert.assertTrue(this.mActivityRule.getActivity().isLocationSwitchClickable());
    }

    @Test
    public void whenPermissionDeniedCannotSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                WelcomeActivity.REQUEST_PERMISSIONS_REQUEST_CODE,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                new int[] { PackageManager.PERMISSION_DENIED });
        Assert.assertFalse(this.mActivityRule.getActivity().isLocationSwitchClickable());
    }

    @Test
    public void whenPermissionCanceledCannotSwitchOnLocation() {
        this.mActivityRule.getActivity().onRequestPermissionsResult(
                WelcomeActivity.REQUEST_PERMISSIONS_REQUEST_CODE,
                new String[] { },
                new int[] { });
        Assert.assertFalse(this.mActivityRule.getActivity().isLocationSwitchClickable());
    }

    @Test
    public void whenPermissionIsForAnOtherActivitySwitchStateNotChanged() {
        boolean before = this.mActivityRule.getActivity().isLocationSwitchClickable();

        this.mActivityRule.getActivity().onRequestPermissionsResult(0,
                new String[] {}, new int[] {});
        Assert.assertThat(this.mActivityRule.getActivity().isLocationSwitchClickable(), is(before));
    }

}

