package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.EmergencyInfoActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
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
public class EmergencyInfoActivityTest {

    final EmergencyInfo info1 = new EmergencyInfo("To much alcool","Seek assistance");
    final EmergencyInfo info2 = new EmergencyInfo("Lost","Check your location on the map");
    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();

    @Before
    public void setup(){
        mock.resetDocument(DatabaseWrapper.EMERGENCY_INFO_PATH);
    }

    @Rule
    public final ActivityTestRule<EmergencyInfoActivity> mActivityRule = new ActivityTestRule<EmergencyInfoActivity>(EmergencyInfoActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppDatabaseWrapper(mock);
        }
    };

    @Test
    public void testEmergencyInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.emergencyInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() {
        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info1);
        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());
        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info1);
    }

    @Ignore("Currently modifying info does not make sens")
    @Test
    public void testCanModifyInfoFromDatabase() throws Throwable {
        mock.addItem(info1);
        mock.modifyItem(info2, 0);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info2);
    }

    @Ignore("Currently modifying info does not make sens")
    @Test
    public void testCantModifyInfoFromDatabaseThatIsNotThere() throws Throwable {
        final EmergencyInfo emergencyInfoModified = new EmergencyInfo();

        mock.addItem(info1);
        mock.modifyItem(emergencyInfoModified, 2);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), emergencyInfoModified);
    }

    @Test
    public void testCanDeleteInfoFromDatabase() {


        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info1);
        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info1);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(1)), info2);
        onView(withId(R.id.emergencyInfoRecyclerView)).check(matches(hasChildCount(2)));

        mock.deleteDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info1);
        onView(withId(R.id.emergencyInfoRecyclerView)).check(matches(hasChildCount(1)));
    }

    @Ignore("What this should do is unclear")
    @Test
    public void testCantDeleteInfoFromEmptyDatabase() throws Throwable {
        final EmergencyInfo info = new EmergencyInfo();
        mock.removeItem(info, 0);
    }

    private void testInfoInView(ViewInteraction viewInteraction, EmergencyInfo information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInstruction()))));
    }
}
