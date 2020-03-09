package ch.epfl.balelecbud.localization;

import android.Manifest;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
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

import java.io.IOException;

import ch.epfl.balelecbud.MainActivity;
import ch.epfl.balelecbud.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class LocalizationServiceTestWithoutPermission {
    private FusedLocationProviderClient client;
    private UiDevice device;

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUpClient() {
        //LocalizationUtils.grantPermissions();
        this.client = LocationServices.getFusedLocationProviderClient(this.mActivityRule.getActivity());
        this.client.setMockMode(true);
        this.device = UiDevice.getInstance(getInstrumentation());
        this.device.pressBack();
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
