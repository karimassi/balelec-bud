package ch.epfl.balelecbud.Location;

import android.Manifest;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.WelcomeActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N_MR1, maxSdkVersion = (Build.VERSION_CODES.Q - 1))
@RunWith(AndroidJUnit4.class)
public class LocationRequesterTest {
    private static long TIMEOUT = 1000;
    private FusedLocationProviderClient client;
    private UiDevice device;

//    private void revokePermissions() {
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
//                "pm revoke " + ApplicationProvider.getApplicationContext().getPackageName() + " "
//                        + Manifest.permission.ACCESS_FINE_LOCATION);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
//                    "pm revoke " + ApplicationProvider.getApplicationContext().getPackageName() + " "
//                            + Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//    }
//
    private void grantPermission() {
//        this.device.waitForIdle();
        if (this.device.hasObject(By.text("ALLOW"))) {
            this.device.findObject(By.text("ALLOW")).click();
            this.device.waitForWindowUpdate(null, TIMEOUT);
        }
    }
//
//    private void denyPermission() {
//        this.device.waitForIdle();
//        if (this.device.hasObject(By.text("DENY")))
//            this.device.findObject(By.text("DENY")).click();
//        this.device.waitForIdle();
//    }

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION
    );

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void setUpClient() {
        this.client = LocationServices.getFusedLocationProviderClient(this.mActivityRule.getActivity());
        this.client.setMockMode(true);
        this.device = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void A_testCanSwitchOnLocalization() {
        grantPermission();
        Assert.assertTrue(this.mActivityRule.getActivity().isLocationSwitchClickable());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertTrue(mActivityRule.getActivity().isLocationActive());
//        revokePermissions();
    }

    @Test
    public void B_testCanSwitchOffLocalization() {
        grantPermission();
        onView(withId(R.id.locationSwitch)).perform(click());
        onView(withId(R.id.locationSwitch)).perform(click());
        Assert.assertFalse(mActivityRule.getActivity().isLocationActive());
//        revokePermissions();
    }

//    @Test
//    public void C_cantActivateLocationWithoutPermission() {
//        denyPermission();
//        Assert.assertFalse(mActivityRule.getActivity().isLocationSwitchClickable());
//        onView(withId(R.id.locationSwitch)).perform(click());
//        Assert.assertFalse(mActivityRule.getActivity().isLocationActive());
//    }
}
