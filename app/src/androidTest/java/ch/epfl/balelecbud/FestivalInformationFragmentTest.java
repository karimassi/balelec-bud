package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationFragmentTest {

    private final MockDatabase mock = MockDatabase.getInstance();

    @Before
    public void setup() {
        BalelecbudApplication.setAppDatabase(mock);
        FragmentScenario.launchInContainer(FestivalInformationFragment.class);
    }

    @After
    public void cleanUp() {
        mock.resetDocument(Database.FESTIVAL_INFORMATION_PATH);
    }

    @Test
    public void testFestivalInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        mock.storeDocument(Database.FESTIVAL_INFORMATION_PATH, info);
        onView(withId(R.id.swipe_refresh_layout_festival_info)).perform(swipeDown());
        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    private void testInfoInView(ViewInteraction viewInteraction, FestivalInformation information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getTitle()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInformation()))));
    }
}