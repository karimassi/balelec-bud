package ch.epfl.balelecbud.Location;

import android.Manifest;
import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.junit.After;
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

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N_MR1, maxSdkVersion = (Build.VERSION_CODES.Q - 1))
@RunWith(AndroidJUnit4.class)
public class LocationServiceTestWithPermission {
    private static final long TIMEOUT_LENGTH = 1000;
    private FusedLocationProviderClient client;
    private UiDevice device;

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<>(WelcomeActivity.class);

    @Rule
    public final GrantPermissionRule fineLocation = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION
    );

    @Before
    public void setUpClient() {
        this.client = LocationServices.getFusedLocationProviderClient(this.mActivityRule.getActivity());
        this.client.setMockMode(true);
//        LocationUtils.grantPermissions();

        this.device = UiDevice.getInstance(getInstrumentation());
//        this.device.waitForIdle();
        if (this.device.hasObject(By.text("ALLOW")))
            this.device.findObject(By.text("ALLOW")).click();
        this.device.waitForIdle();
    }

    @After
    public void tearDown() {
//        this.device.waitForIdle();
        LocationUtils.revokePermissions();
//        this.device.waitForIdle();
    }

    @Test
    public void testCanSwitchOnLocalization() {
        Assert.assertTrue(this.mActivityRule.getActivity().isLocationSwitchClickable());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertTrue(mActivityRule.getActivity().isLocationActive());
    }

    @Test
    public void testCanSwitchOffLocalization() {
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertFalse(mActivityRule.getActivity().isLocationActive());
    }
}