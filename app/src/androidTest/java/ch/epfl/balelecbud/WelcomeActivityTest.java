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
import ch.epfl.balelecbud.location.LocationUtil;

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
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    LocationUtil.setLocationClient(new LocationClient() {
                        @Override
                        public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {

                        }

                        @Override
                        public void removeLocationUpdates(PendingIntent intent) {

                        }
                    });
                }
            };

    @Before
    public void setup() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    @Test
    public void testMapButtonIsDisplayed() {
        onView(withId(R.id.mapButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mapButton)).perform(click());
    }

    @Test
    public void testInfoButtonIsDisplayed() {
        onView(withId(R.id.infoButton)).check(matches(isDisplayed()));
        onView(withId(R.id.infoButton)).perform(click());
    }

    @Test
    public void testTransportButtonIsDisplayed() {
        onView(withId(R.id.transportButton)).check(matches(isDisplayed()));
        onView(withId(R.id.transportButton)).perform(click());
    }

    @Test
    public void testScheduleButtonIsDisplayed() {
        onView(withId(R.id.scheduleButton)).check(matches(isDisplayed()));
        onView(withId(R.id.scheduleButton)).perform(click());
    }

    @Test
    public void testPOIButtonIsDisplayed() {
        onView(withId(R.id.poiButton)).check(matches(isDisplayed()));
        onView(withId(R.id.poiButton)).perform(click());
    }

    @Test
    public void testSignOutIsDisplayed() {
        onView(withId(R.id.buttonSignOut)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonSignOut)).perform(click());
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
    public void testPOIIsDisplayed() {
        testFeatureIsDisplayed(onView(withId(R.id.poiButton)), onView(withId(R.id.pointOfInterestRecyclerView)));
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

    private void testFeatureIsDisplayed(ViewInteraction button, ViewInteraction feature) {
        button.perform(click());
        feature.check(matches(isDisplayed()));
        button.check(doesNotExist());
    }
}
