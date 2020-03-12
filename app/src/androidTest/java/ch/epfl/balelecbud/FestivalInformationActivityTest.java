package ch.epfl.balelecbud;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.protobuf.NullValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.festivalInformation.BasicDatabase;
import ch.epfl.balelecbud.festivalInformation.FestivalInformation;
import ch.epfl.balelecbud.festivalInformation.FestivalInformationAdapter;
import ch.epfl.balelecbud.festivalInformation.FestivalInformationListener;
import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationActivityTest {

    FestivalInformationListener festivalInfoListener;

    private void testInfoInView(ViewInteraction viewInteraction, FestivalInformation information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getTitle()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInformation()))));
    }

    @Before
    public void setUp() {
        BasicDatabase mockDatabase = new BasicDatabase() {
            @Override
            public void registerListener(FestivalInformationListener listener) {
                festivalInfoListener = listener;
            }

            @Override
            public void unregisterListener() {
            }

            @Override
            public void listen() {
            }
        };
        FestivalInformationAdapter.setDatabaseImplementation(mockDatabase);
        ActivityScenario activityScenario = ActivityScenario.launch(FestivalInformationActivity.class);
    }

    @Test
    public void testFestivalInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FestivalInformationListener.addInformation(info);
            }
        };

        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }

    @Test
    public void testCanModifyInfoFromDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("New", "Hello it's a me, new");
        final FestivalInformation infoModified = new FestivalInformation("Modified", "Hello it's a me, new");

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FestivalInformationListener.addInformation(info);
                FestivalInformationListener.modifyInformation(infoModified, 0);
            }
        };

        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), infoModified);
    }

    @Test
    public void testCanDeleteInfoFromDatabase() throws Throwable {
        final FestivalInformation info = new FestivalInformation("Bad", "Hello it's a me, bad");

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FestivalInformationListener.addInformation(info);
                FestivalInformationListener.addInformation(info);
                FestivalInformationListener.removeInformation(info, 1);
            }
        };

        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }

        testInfoInView(onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)), info);
    }
}