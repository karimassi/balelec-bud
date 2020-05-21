package ch.epfl.balelecbud.view;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.view.welcome.WelcomeFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertNull;

@RunWith(AndroidJUnit4.class)
public class WelcomeFragmentTest {

    @Before
    public void setup() {
        FragmentScenario.launchInContainer(WelcomeFragment.class);
    }

    @Test
    public void backgroundIsDisplayed() {
        onView(ViewMatchers.withId(R.id.activity_home_linear_layout)).check(matches(isDisplayed()));
    }
}