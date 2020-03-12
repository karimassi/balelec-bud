package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import ch.epfl.balelecbud.FestivalInformation.FestivalInformation;
import ch.epfl.balelecbud.FestivalInformation.FestivalInformationListener;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

@RunWith(AndroidJUnit4.class)
public class FestivalInformationActivityTest {

    @Rule
    public final ActivityTestRule<FestivalInformationActivity> fActivityRule =
            new ActivityTestRule<>(FestivalInformationActivity.class);

    @Test
    public void testFestivalInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));
    }
}