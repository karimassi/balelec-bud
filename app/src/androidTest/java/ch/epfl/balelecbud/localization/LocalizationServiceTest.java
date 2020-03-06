package ch.epfl.balelecbud.localization;

import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import androidx.annotation.RequiresPermission;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.permission.PermissionRequester;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.MainActivity;
import ch.epfl.balelecbud.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class LocalizationServiceTest {
    private FusedLocationProviderClient client;

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUpClient() {
        this.client = LocationServices.getFusedLocationProviderClient(this.mActivityRule.getActivity());
        this.client.setMockMode(true);
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        if (mDevice.hasObject(By.text("ALLOW")))
            mDevice.findObject(By.text("ALLOW")).click();
    }

    @After
    public void tearDown() {
        if (mActivityRule.getActivity().isLocalizationActive())
            onView(withId(R.id.localizationSwitch)).perform(click());
    }



    @Test
    public void testCanSwitchOnLocalization() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        Assert.assertTrue(mActivityRule.getActivity().isLocalizationActive());
    }

    @Test
    public void testCanSwitchOffLocalization() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        onView(withId(R.id.localizationSwitch)).perform(click());
        Assert.assertFalse(mActivityRule.getActivity().isLocalizationActive());
    }

    @Test
    public void sendANullLocation() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        client.setMockLocation(null);
        client.flushLocations();
    }

    @Test
    public void sendValidLocationViaGPS() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(1.2797677);
        mockLocation.setLongitude(103.8459285);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        client.setMockLocation(mockLocation);
        client.flushLocations();
    }

    @Test
    public void sendValidLocationViaNetworkProvider() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        Location mockLocation = new Location(LocationManager.NETWORK_PROVIDER);
        mockLocation.setLatitude(42.0);
        mockLocation.setLongitude(50.42);
        mockLocation.setAltitude(4);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(0.5f);
        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        client.setMockLocation(mockLocation);
        client.flushLocations();
    }

    @Test
    public void sendValidLocationViaPassiveProvider() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        Location mockLocation = new Location(LocationManager.PASSIVE_PROVIDER);
        mockLocation.setLatitude(2.0);
        mockLocation.setLongitude(55.42);
        mockLocation.setAltitude(42);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(4.5f);
        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        client.setMockLocation(mockLocation);
        client.flushLocations();
    }
}