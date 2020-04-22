package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
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
import static ch.epfl.balelecbud.testUtils.CustomMatcher.nthChildOf;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationFragmentTest extends RootActivityTest {

    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();

    @Before
    public final void openInfoFragment(){
        RootActivityTest.openDrawer();
        RootActivityTest.clickItem(R.id.activity_main_drawer_info, R.id.festivalInfoRecyclerView);
    }

    @Before
    public void setup() {
        mock.resetDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH);
    }

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule = new ActivityTestRule<RootActivity>(RootActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppDatabaseWrapper(mock);
        }
    };

    @Test
    public void testFestivalInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase(){
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        mock.storeDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info);
        onView(withId(R.id.swipe_refresh_layout_festival_info)).perform(swipeDown());
        testInfoInView(onView(nthChildOf(withId(R.id.festivalInfoRecyclerView), 0)), info);
        //testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCanDeleteInfoFromDatabase() {
        final FestivalInformation info1 = new FestivalInformation("Bad", "Hello it's a me, bad");
        final FestivalInformation info2 = new FestivalInformation("Good", "Hello it's a me, good");

        mock.storeDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info1);
        mock.storeDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_festival_info)).perform(swipeDown());

        //testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info1);
        //testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(1)), info2);
        testInfoInView(onView(nthChildOf(withId(R.id.festivalInfoRecyclerView), 0)), info1);
        testInfoInView(onView(nthChildOf(withId(R.id.festivalInfoRecyclerView), 1)), info2);
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(hasChildCount(2)));

        mock.deleteDocument(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_festival_info)).perform(swipeDown());

        //testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info1);
        testInfoInView(onView(nthChildOf(withId(R.id.festivalInfoRecyclerView), 0)), info1);
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(hasChildCount(1)));
    }

    private void testInfoInView(ViewInteraction viewInteraction, FestivalInformation information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getTitle()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInformation()))));
    }
}