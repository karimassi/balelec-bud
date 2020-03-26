package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.festivalInformation.FestivalInformationAdapter;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationActivityTest {

    MockDatabaseWrapper mock;

    @Rule
    public final ActivityTestRule<FestivalInformationActivity> mActivityRule = new ActivityTestRule<FestivalInformationActivity>(FestivalInformationActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
            FestivalInformationAdapter.setDatabaseImplementation(mock);
        }
    };

    @Test
    public void testFestivalInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        mock.addItem(info);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCanModifyInfoFromDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        final FestivalInformation infoModified = new FestivalInformation("Modified", "Hello it's a me, new");

        mock.addItem(info);
        mock.modifyItem(infoModified, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), infoModified);
    }

    @Test
    public void testCantModifyInfoFromDatabaseThatIsNotThere() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        final FestivalInformation infoModified = new FestivalInformation();

        mock.addItem(info);
        mock.modifyItem(infoModified, 2);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCanDeleteInfoFromDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("Bad", "Hello it's a me, bad");

        mock.addItem(info);
        mock.addItem(info);
        mock.removeItem(info, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCantDeleteInfoFromEmptyDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation();
        mock.removeItem(info, 0);
    }

    private void testInfoInView(ViewInteraction viewInteraction, FestivalInformation information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getTitle()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInformation()))));
    }
}