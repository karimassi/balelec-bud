package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
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
public class EmergencyInfoActivityTest extends BasicActivityTest{

    final EmergencyInfo info1 = new EmergencyInfo("To much alcohol","Seek assistance");
    final EmergencyInfo info2 = new EmergencyInfo("Lost","Check your location on the map");
    private final MockDatabase mock = MockDatabase.getInstance();

    @Rule
    public final ActivityTestRule<EmergencyInfoActivity> mActivityRule = new ActivityTestRule<EmergencyInfoActivity>(EmergencyInfoActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppDatabase(mock);
        }
    };

    @Before
    public void setup(){
        mock.resetDocument(Database.EMERGENCY_INFO_PATH);
    }

    @Test
    public void testEmergencyInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.emergencyInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() {
        mock.storeDocument(Database.EMERGENCY_INFO_PATH, info1);
        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());
        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info1);
    }

    private void testInfoInView(ViewInteraction viewInteraction, EmergencyInfo information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInstruction()))));
    }

    @Override
    protected void setIds() {
        setIds(R.id.emergency_info_activity_drawer_layout, R.id.emergency_info_activity_nav_view);
    }
}
