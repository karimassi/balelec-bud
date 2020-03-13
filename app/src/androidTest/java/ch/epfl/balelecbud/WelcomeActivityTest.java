package ch.epfl.balelecbud;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

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
            new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void grantPermission() throws IOException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.executeShellCommand("pm reset-permissions");
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    @Test
    public void testCanLogOut() {
        onView(withId(R.id.buttonSignOut)).perform(click());
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void testMapButtonIsDisplayed() {
        onView(withId(R.id.mapButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mapButton)).perform(click());
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.mapButton)).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId((R.id.mapButton))).check(doesNotExist());
    }
}
