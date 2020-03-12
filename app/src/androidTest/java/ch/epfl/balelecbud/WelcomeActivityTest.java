package ch.epfl.balelecbud;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void testMapButtonIsDisplayed() {
        onView(withId(R.id.mapButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mapButton)).perform(click());
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
