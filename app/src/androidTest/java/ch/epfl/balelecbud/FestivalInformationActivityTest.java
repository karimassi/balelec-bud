package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.festivalInformation.FestivalInformation;
import ch.epfl.balelecbud.festivalInformation.FestivalInformationAdapter;
import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationActivityTest {

    MockDatabaseWrapper mock;

    @Rule
    public final ActivityTestRule<FestivalInformationActivity> mActivityRule = new ActivityTestRule<FestivalInformationActivity>(FestivalInformationActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = new MockDatabaseWrapper();
            FestivalInformationAdapter.setDatabaseImplementation(mock);
        }
    };

    private void addItem(final FestivalInformation information) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mock.addItem(information);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }

    private void modifyItem(final FestivalInformation information, final int index) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mock.changeItem(information, index);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }

    private void removeItem(final FestivalInformation information, final int index) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mock.removeItem(information, index);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }

    @Test
    public void testFestivalInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        addItem(info);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCanModifyInfoFromDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        final FestivalInformation infoModified = new FestivalInformation("Modified", "Hello it's a me, new");

        addItem(info);
        modifyItem(infoModified, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), infoModified);
    }

    @Test
    public void testCantModifyInfoFromDatabaseThatIsNotThere() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        final FestivalInformation infoModified = new FestivalInformation();

        addItem(info);
        modifyItem(infoModified, 2);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCanDeleteInfoFromDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("Bad", "Hello it's a me, bad");

        addItem(info);
        addItem(info);
        removeItem(info, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCantDeleteInfoFromEmptyDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation();
        removeItem(info, 0);
    }

    private void testInfoInView(ViewInteraction viewInteraction, FestivalInformation information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getTitle()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInformation()))));
    }


}