package ch.epfl.balelecbud.localization;

import android.app.Instrumentation;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

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
public class LocalizationServiceTestWithPermission {
    private static final long TIMEOUT_LENGTH = 1000;
    private FusedLocationProviderClient client;
    private UiDevice device;

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUpClient() {
        LocalizationUtils.grantPermissions();

        this.client = LocationServices.getFusedLocationProviderClient(this.mActivityRule.getActivity());
        this.client.setMockMode(true);
        this.device = UiDevice.getInstance(getInstrumentation());
        this.device.pressBack();
//        this.device.wait(Until.hasObject(By.text("ALLOW")), TIMEOUT_LENGTH);
//        if (this.device.hasObject(By.text("ALLOW")))
//            this.device.findObject(By.text("ALLOW")).click();
        device.waitForIdle();
    }

    @After
    public void tearDown() {
        LocalizationUtils.revokePermissions();
    }

    @Test
    public void testCanSwitchOnLocalization() {
        device.wait(Until.hasObject(By.clickable(true)), TIMEOUT_LENGTH);
        Assert.assertTrue(this.mActivityRule.getActivity().isLocalizationSwitchClickable());
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

//    @Test
//    public void pauseAndRestartActivityWithNullBundle() {
//        Instrumentation inst = InstrumentationRegistry.getInstrumentation();
//        this.device.pressHome();
//        inst.callActivityOnDestroy(this.mActivityRule.getActivity());
//        inst.callActivityOnCreate(this.mActivityRule.getActivity(), null);
//        Assert.assertFalse(this.mActivityRule.getActivity().isLocalizationActive());
//    }
//
//
//    @Test
//    public void pauseAndRestartActivityWithBundleWithFalseValue() {
//        Instrumentation inst = InstrumentationRegistry.getInstrumentation();
//        inst.callActivityOnDestroy(this.mActivityRule.getActivity());
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(MainActivity.LAST_STATE, false);
//        inst.callActivityOnCreate(this.mActivityRule.getActivity(), bundle);
//        Assert.assertFalse(this.mActivityRule.getActivity().isLocalizationActive());
//    }
//
//    @Test
//    public void pauseAndRestartActivityWithBundleWithTrueValue() {
//        Instrumentation inst = InstrumentationRegistry.getInstrumentation();
//        inst.callActivityOnDestroy(this.mActivityRule.getActivity());
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(MainActivity.LAST_STATE, true);
//        inst.callActivityOnCreate(this.mActivityRule.getActivity(), bundle);
//        Assert.assertTrue(this.mActivityRule.getActivity().isLocalizationActive());
//    }
//
//    @Test
//    public void pauseAndRestartActivityWithPersistentBundleWithTrueValue() {
//        Instrumentation inst = InstrumentationRegistry.getInstrumentation();
//        inst.callActivityOnDestroy(this.mActivityRule.getActivity());
//        PersistableBundle bundle = new PersistableBundle();
//        bundle.putBoolean(MainActivity.LAST_STATE, true);
//        inst.callActivityOnCreate(this.mActivityRule.getActivity(), null, bundle);
//        Assert.assertTrue(this.mActivityRule.getActivity().isLocalizationActive());
//    }
//
//    @Test
//    public void pauseAndRestartActivityWithPersistentBundleWithFalseValue() {
//        Instrumentation inst = InstrumentationRegistry.getInstrumentation();
//        inst.callActivityOnDestroy(this.mActivityRule.getActivity());
//        PersistableBundle bundle = new PersistableBundle();
//        bundle.putBoolean(MainActivity.LAST_STATE, false);
//        inst.callActivityOnCreate(this.mActivityRule.getActivity(), null, bundle);
//        Assert.assertFalse(this.mActivityRule.getActivity().isLocalizationActive());
//    }
//
//
//    @Test
//    public void pauseAndRestartActivityWithNullPersistentBundle() {
//        Instrumentation inst = InstrumentationRegistry.getInstrumentation();
//        inst.callActivityOnDestroy(this.mActivityRule.getActivity());
//        inst.callActivityOnCreate(this.mActivityRule.getActivity(), null, null);
//        Assert.assertTrue(this.mActivityRule.getActivity().isLocalizationActive());
//    }
//
//    @Test
//    public void pauseAndRestartActivityWithPersistentAndNormalBundle() {
//        Instrumentation inst = InstrumentationRegistry.getInstrumentation();
//        inst.callActivityOnDestroy(this.mActivityRule.getActivity());
//        PersistableBundle pb = new PersistableBundle();
//        pb.putBoolean(MainActivity.LAST_STATE, true);
//        Bundle b = new Bundle();
//        b.putBoolean(MainActivity.LAST_STATE, false);
//        inst.callActivityOnCreate(this.mActivityRule.getActivity(), null, pb);
//        Assert.assertTrue(this.mActivityRule.getActivity().isLocalizationActive());
//    }
}