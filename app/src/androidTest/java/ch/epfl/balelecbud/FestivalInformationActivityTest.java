package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.festivalInformation.FestivalInformationData;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationActivityTest {

    private MockDatabaseWrapper mock;

    @Before
    public void setup(){
        mock.resetDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH);
    }

    @Rule
    public final ActivityTestRule<FestivalInformationActivity> mActivityRule = new ActivityTestRule<FestivalInformationActivity>(FestivalInformationActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
            FestivalInformationData.setDatabaseImplementation(mock);
        }
    };

    @Test
    public void testFestivalInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        mock.storeDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info);
        onView(withId(R.id.swipe_refresh_layout_festival_info)).perform(swipeDown());
        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Ignore("Currently modifying info does not make sens")
    @Test
    public void testCanModifyInfoFromDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        final FestivalInformation infoModified = new FestivalInformation("Modified", "Hello it's a me, new");

        mock.addItem(info);
        mock.modifyItem(infoModified, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), infoModified);
    }

    @Ignore("Currently modifying info does not make sens")
    @Test
    public void testCantModifyInfoFromDatabaseThatIsNotThere() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        final FestivalInformation infoModified = new FestivalInformation();

        mock.addItem(info);
        mock.modifyItem(infoModified, 2);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCanDeleteInfoFromDatabase() {
        final FestivalInformation info1 = new FestivalInformation("Bad", "Hello it's a me, bad");
        final FestivalInformation info2 = new FestivalInformation("Good", "Hello it's a me, good");

        mock.storeDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info1);
        mock.storeDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_festival_info)).perform(swipeDown());

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info1);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(1)), info2);
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(hasChildCount(2)));

        mock.deleteDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_festival_info)).perform(swipeDown());

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info1);
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(hasChildCount(1)));
    }

    @Ignore("What this should do is unclear")
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