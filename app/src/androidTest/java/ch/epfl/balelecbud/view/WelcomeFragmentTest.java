package ch.epfl.balelecbud.view;

import android.os.SystemClock;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.HelpPage;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.view.welcome.WelcomeFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickChildViewWithId;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickTabWithPosition;
import static ch.epfl.balelecbud.utility.json.JsonResourceReader.getHelpPageCollection;
import static junit.framework.TestCase.assertNull;

@RunWith(AndroidJUnit4.class)
public class WelcomeFragmentTest {

    @Before
    public void setup() {
        FragmentScenario.launchInContainer(WelcomeFragment.class, null, R.style.Theme_AppCompat, null);
    }

    @Test
    public void backgroundIsDisplayed() {
        onView(ViewMatchers.withId(R.id.activity_home_linear_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void pageViewerIsDisplayed() {
        onView(ViewMatchers.withId(R.id.welcome_view_pager)).check(matches(isDisplayed()));
    }

    @Test
    public void testPageViewer() {
        List<HelpPage> pages = getHelpPageCollection(R.raw.help_page);
        onView(withId(R.id.welcome_view_pager)).check(matches(hasDescendant(withText(pages.get(0).getTitle()))));

        onView(withId(R.id.welcome_view_pager)).perform(ViewActions.swipeLeft());
        SystemClock.sleep(500);

        onView(withId(R.id.welcome_view_pager)).check(matches(hasDescendant(withText(pages.get(1).getTitle()))));
    }

    @Test
    public void testTabIndicator() {
        List<HelpPage> pages = getHelpPageCollection(R.raw.help_page);

        onView(withId(R.id.welcome_tab_indicator)).perform(clickTabWithPosition(1));
        SystemClock.sleep(500);

        onView(withId(R.id.welcome_view_pager)).check(matches(hasDescendant(withText(pages.get(1).getTitle()))));
    }
}