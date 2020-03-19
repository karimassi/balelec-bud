package ch.epfl.balelecbud;

import android.view.View;
import android.view.ViewGroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

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

        Slot slot3 = new Slot("Upset", "Scène Sat'", "19h - 20h");
        mock.addItem(slot3);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(3)));

        mock.modifyItem(slot3, 0);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 0)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1)).check(matches(withText("Upset")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 2)).check(matches(withText("Scène Sat'")));

        mock.removeItem(slot3, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(2)));

        mock.removeItem(slot2, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(1)));

        mock.removeItem(slot3, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(0)));
    }

    @Test
    public void testCaseForRecyclerItems() throws Throwable {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Slot slot2 = new Slot("Walking Furret", "Les Azimutes", "20h - 21h");
        Slot slot3 = new Slot("Upset", "Scène Sat'", "19h - 20h");

        mock.addItem(slot1);
        mock.addItem(slot2);
        mock.addItem(slot3);

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 0)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1)).check(matches(withText("Mr Oizo")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 2)).check(matches(withText("Grande scène")));

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 0)).check(matches(withText("20h - 21h")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1)).check(matches(withText("Walking Furret")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 2)).check(matches(withText("Les Azimutes")));

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 0)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 1)).check(matches(withText("Upset")));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 2)).check(matches(withText("Scène Sat'")));
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
}
