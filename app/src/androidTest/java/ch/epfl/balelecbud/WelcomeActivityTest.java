package ch.epfl.balelecbud;


import android.app.PendingIntent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.location.LocationClient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {
    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void setup() {
        WelcomeActivity.mockMode = true;
        mActivityRule.getActivity().setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {

            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {

            }
        });
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    @Test
    public void testMapButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.mapButton)));
    }

    @Test
    public void testInfoButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.infoButton)));
    }

    @Test
    public void testTransportButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.transportButton)));
    }

    @Test
    public void testScheduleButtonIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.scheduleButton)));
    }

    @Test
    public void testSignOutIsDisplayed() {
        testButtonIsDisplayed(onView(withId(R.id.buttonSignOut)));
    }

    @Test
    public void testToggleLocationIsDisplayed() {
        onView(withId(R.id.locationSwitch)).check(matches(isDisplayed()));
        onView(withId(R.id.locationSwitch)).perform(click());
    }

    @Test
    public void testMapIsDisplayed() {
        testFeatureIsDisplayed(onView(withId(R.id.mapButton)), onView(withId((R.id.mapLinearLayout))));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testInfoIsDisplayed() {
        testFeatureIsDisplayed(onView(withId(R.id.infoButton)), onView(withId(R.id.festivalInfoRecyclerView)));
    }

    @Test
    public void testTransportIsDisplayed() {
        testFeatureIsDisplayed(onView(withId(R.id.transportButton)), onView(withId(R.id.transportLinearLayout)));
        onView(withId(R.id.fragmentTransportList)).check(matches(isDisplayed()));
    }

    @Test
    public void testScheduleIsDisplayed() {
        testFeatureIsDisplayed(onView(withId(R.id.scheduleButton)), onView(withId(R.id.scheduleRecyclerView)));
    }

    @Test
    public void testLoginScreenDisplayed() {
        testFeatureIsDisplayed(onView(withId(R.id.buttonSignOut)), onView(withId(R.id.loginLinearLayout)));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    private void testButtonIsDisplayed(ViewInteraction button) {
        button.check(matches(isDisplayed()));
        button.perform(click());
    }

    private void testFeatureIsDisplayed(ViewInteraction button, ViewInteraction feature) {
        button.perform(click());
        feature.check(matches(isDisplayed()));
        button.check(doesNotExist());
    }
}
