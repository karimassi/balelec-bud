package ch.epfl.balelecbud;


import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule = new ActivityTestRule<>(WelcomeActivity.class);

//    @Before
//    public void grantPermission() throws IOException {
//        UiDevice device = UiDevice.getInstance(getInstrumentation());
//        device.executeShellCommand("pm reset-permissions");
//        if (device.hasObject(By.text("ALLOW"))) {
//            device.findObject(By.text("ALLOW")).click();
//            device.waitForWindowUpdate(null, 1000);
//        }
//    }
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    );

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
    public void testScheduleButtonIsDisplayed(){
        onView(withId(R.id.scheduleButton)).check(matches(isDisplayed()));
        onView(withId(R.id.scheduleButton)).perform(click());
    }

    @Test
    public void testSignOutIsDisplayed(){
        onView(withId(R.id.buttonSignOut)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonSignOut)).perform(click());
    }

    @Test
    public void testToggleLocationIsDisplayed(){
        onView(withId(R.id.locationSwitch)).check(matches(isDisplayed()));
        onView(withId(R.id.locationSwitch)).perform(click());
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.mapButton)).perform(click());
        onView(withId(R.id.maplinearlayout)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId((R.id.mapButton))).check(doesNotExist());
    }

    @Test
    public void testInfoIsDisplayed() {
        onView(withId(R.id.infoButton)).perform(click());
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
        onView(withId((R.id.infoButton))).check(doesNotExist());
    }

    @Test
    public void testTransportIsDisplayed() {
        //onView(withId(R.id.transportButton)).perform(click());
        //onView(withId(R.id.maplinearlayout)).check(matches(isDisplayed()));
        //onView(withId(R.id.map)).check(matches(isDisplayed()));
        //onView(withId((R.id.transportButton))).check(doesNotExist());
    }

    @Test
    public void testScheduleIsDisplayed(){
        onView(withId(R.id.scheduleButton)).perform(click());
        onView(withId(R.id.rvSchedule)).check(matches(isDisplayed()));
        onView(withId((R.id.scheduleButton))).check(doesNotExist());
    }

    @Test
    public void testLoginScreenDisplayed(){
        onView(withId(R.id.buttonSignOut)).perform(click());
        onView(withId(R.id.loginLinearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
        onView(withId((R.id.buttonSignOut))).check(doesNotExist());
    }
}
