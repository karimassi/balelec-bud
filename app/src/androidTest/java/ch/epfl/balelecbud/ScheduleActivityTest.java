package ch.epfl.balelecbud;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {

    MockDatabaseWrapper mock;

    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule = new ActivityTestRule<ScheduleActivity>(ScheduleActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = new MockDatabaseWrapper();
            ScheduleAdapter.setDatabaseImplementation(mock);
        }
    };

    @Test
    public void testRecyclerViewVisible() {
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testItemModification() throws Throwable {

        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(0)));

        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        mock.addItem(slot1);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(1)));

        Slot slot2 = new Slot("Walking Furret", "Les Azimutes", "20h - 21h");
        mock.addItem(slot2);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(2)));

        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 0)).check(matches(withText("Mr Oizo")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 1)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 2)).check(matches(withText("Grande scène")));

        mock.modifyItem(slot2, 0);
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 0)).check(matches(withText("Walking Furret")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 1)).check(matches(withText("20h - 21h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 2)).check(matches(withText("Les Azimutes")));

        mock.removeItem(slot2, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(1)));
    }

    @Test
    public void testCaseForRecyclerItems() throws Throwable {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Slot slot2 = new Slot("Walking Furret", "Les Azimutes", "20h - 21h");

        mock.addItem(slot1);
        mock.addItem(slot2);

        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 0)).check(matches(withText("Mr Oizo")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 1)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 2)).check(matches(withText("Grande scène")));

        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1), 0)).check(matches(withText("Walking Furret")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1), 1)).check(matches(withText("20h - 21h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1), 2)).check(matches(withText("Les Azimutes")));
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with " + childPosition + " child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
            }
        };
    }

    @Test
    public void testDrawer() {
        onView(withId(R.id.schedule_activity_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_info));
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));

    }

    @Test
    public void openScheduleActivityFromDrawer() {
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_schedule));
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openPOIActivityFromDrawer() {
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_poi));
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openMapActivityFromDrawer() {
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_map));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void openTransportActivityFromDrawer() {
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_transport));
        onView(withId(R.id.fragmentTransportList)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutFromDrawer() {
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
    }

    @Test
    public void testBackPress(){
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.schedule_activity_nav_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.schedule_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
    }
}
