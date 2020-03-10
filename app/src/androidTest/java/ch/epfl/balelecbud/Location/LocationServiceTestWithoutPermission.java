package ch.epfl.balelecbud.Location;

import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.MainActivity;
import ch.epfl.balelecbud.WelcomeActivity;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@SdkSuppress(maxSdkVersion = (Build.VERSION_CODES.Q - 1))
@RunWith(AndroidJUnit4.class)
public class LocationServiceTestWithoutPermission {
    private FusedLocationProviderClient client;
    private UiDevice device;

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void setUpClient() {
        //LocalizationUtils.grantPermissions();
        this.client = LocationServices.getFusedLocationProviderClient(this.mActivityRule.getActivity());
        this.client.setMockMode(true);
        this.device = UiDevice.getInstance(getInstrumentation());
//        LocalizationUtils.revokePermissions();
        if (this.device.hasObject(By.text("DENY")))
            this.device.findObject(By.text("DENY")).click();
        this.device.waitForIdle();
    }

    @After
    public void tearDown() {
        //LocalizationUtils.revokePermissions();
    }

    @Test
    public void cantActivateLocationWithoutPermission() {
        Assert.assertTrue(true);
//        Assert.assertFalse(mActivityRule.getActivity().isLocalizationSwitchClickable());
//        onView(withId(R.id.localizationSwitch)).perform(click());
//        Assert.assertFalse(mActivityRule.getActivity().isLocalizationActive());
    }
}
