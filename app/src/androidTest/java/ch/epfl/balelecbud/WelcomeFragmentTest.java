package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class WelcomeFragmentTest {

    @Before
    public void setup() {
        FragmentScenario.launchInContainer(WelcomeFragment.class);
    }

    @Test
    public void backGroundIsDisplayed() {
        onView(withId(R.id.activity_home_linear_layout)).check(matches(isDisplayed()));
    }
}