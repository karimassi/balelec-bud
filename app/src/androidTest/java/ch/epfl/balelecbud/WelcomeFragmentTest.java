package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class WelcomeFragmentTest extends RootActivityTest {
    @Test
    public void testToggleLocationIsDisplayed() {
        onView(withId(R.id.locationSwitch)).check(matches(isDisplayed()));
        onView(withId(R.id.locationSwitch)).perform(click());
    }

    @Override
    protected int getItemId() {
        return R.id.activity_main_drawer_home;
    }

    @Override
    protected int getViewToDisplayId() {
        return R.id.activity_home_linear_layout;
    }
}
