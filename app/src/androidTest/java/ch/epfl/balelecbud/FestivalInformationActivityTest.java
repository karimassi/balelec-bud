package ch.epfl.balelecbud;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.FestivalInformation.BasicDatabase;
import ch.epfl.balelecbud.FestivalInformation.FestivalInformation;
import ch.epfl.balelecbud.FestivalInformation.FestivalInformationAdapter;
import ch.epfl.balelecbud.FestivalInformation.FestivalInformationListener;
import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationActivityTest {

    FestivalInformationListener festivalInfoListener;

    @Before
    public void setUp() {
        BasicDatabase mockDatabase = new BasicDatabase() {
            @Override
            public void registerListener(FestivalInformationListener listener) {
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
    public void testCanModifyInfoFromDatabase() throws Throwable {
        final FestivalInformation information = new FestivalInformation("New", "Hello it's a me, new");
        final int index = 0;

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                festivalInfoListener.modifyInformation(information, index);
            }
        };

        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }

        onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)).check(matches(hasDescendant(withText(information.getTitle()))));
        onView(new RecyclerViewMatcher(R.id.festivalInfoRecyclerView).atPosition(0)).check(matches(hasDescendant(withText(information.getInformation()))));
    }
}