package ch.epfl.balelecbud.emergency;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.EmergencyInfoActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EmergencyInfoRecyclerViewTest {

    final EmergencyInfo info1 = new EmergencyInfo("To much alcool","Seek assistance");
    final EmergencyInfo info2 = new EmergencyInfo("Lost","Check your location on the map");
    final EmergencyInfo info3 = new EmergencyInfo("Fire","Call firefighters");

    MockDatabaseWrapper mock;

    @Rule
    public final ActivityTestRule<EmergencyInfoActivity> mActivityRule = new ActivityTestRule<EmergencyInfoActivity>(EmergencyInfoActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = new MockDatabaseWrapper();
            MyEmergencyInfoRecyclerViewAdapter.setDatabaseImplementation(mock);
        }
    };

    private void compareViewAndItem(ViewInteraction viewInt, EmergencyInfo emergencyInfo){
        viewInt.check(matches(hasDescendant(withText(emergencyInfo.getName()))));
        viewInt.check(matches(hasDescendant(withText(emergencyInfo.getInstruction()))));
    }

    @Test
    public void displayIsUpdatedWhenItemsAdded() throws Throwable {

        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(0)));

        mock.addItem(info1);

        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(0)), info1);
        onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(0)).perform(click());

        mock.addItem(info2);
        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(2)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(0)), info1);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(1)), info2);

        mock.addItem(info3);
        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(3)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(0)), info1);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(1)), info2);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(2)), info3);
    }

    @Test
    public void displayIsUpdatedWhenItemsModified() throws Throwable {

        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(0)));

        mock.addItem(info1);

        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(0)), info1);

        mock.modifyItem(info2, 0);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(0)), info2);
    }

    @Test
    public void displayIsUpdatedWhenItemsRemoved() throws Throwable {

        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(0)));

        mock.addItem(info1);

        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragmentEmergencyInfoList).atPosition(0)), info1);

        mock.removeItem(info1, 0);

        onView(withId(R.id.fragmentEmergencyInfoList)).check(matches(hasChildCount(0)));
    }

    @Test
    public void canCreateOtherFragment() {
        EmergencyInfoListFragment.newInstance();
    }

}
