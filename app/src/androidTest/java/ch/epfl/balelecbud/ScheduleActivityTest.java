package ch.epfl.balelecbud;

import android.view.View;
import android.view.ViewGroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    static private Slot slot1;
    static private Slot slot2;
    static private Slot slot3;

    @BeforeClass
    public static void setUpSlots(){
        List<Timestamp> timestamps = new LinkedList<>();
        for(int i = 0; i < 6; ++i){
            Calendar c = Calendar.getInstance();
            c.set(2020,11,11,10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot("Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot("Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3)) ;
        slot3 = new Slot("Upset", "Scène Sat'",  timestamps.get(4), timestamps.get(5));
    }
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

        mock.addItem(slot1);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(1)));

        mock.addItem(slot2);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(2)));

        mock.addItem(slot3);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(3)));

<<<<<<< HEAD
        modifyItem(slot3, 0);
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 2)).check(matches(withText(slot3.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 3)).check(matches(isDisplayed()));
=======
        mock.modifyItem(slot3, 0);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 2)).check(matches(withText(slot3.getSceneName())));
>>>>>>> c48e36e233bfa3dd77b24bc30bb1ef28db742b4d

        mock.removeItem(slot3, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(2)));

        mock.removeItem(slot2, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(1)));

        mock.removeItem(slot3, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(0)));
    }

    @Test
    public void testCaseForRecyclerItems() throws Throwable {

        mock.addItem(slot1);
        mock.addItem(slot2);
        mock.addItem(slot3);

<<<<<<< HEAD
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 0)).check(matches(withText(slot1.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 1)).check(matches(withText(slot1.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 2)).check(matches(withText(slot1.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 3)).check(matches(isDisplayed()));

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 0)).check(matches(withText(slot2.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 1)).check(matches(withText(slot2.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 2)).check(matches(withText(slot2.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 3)).check(matches(isDisplayed()));

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 2)).check(matches(withText(slot3.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 3)).check(matches(isDisplayed()));

=======
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 0)).check(matches(withText(slot1.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1)).check(matches(withText(slot1.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 2)).check(matches(withText(slot1.getSceneName())));

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 0)).check(matches(withText(slot2.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1)).check(matches(withText(slot2.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 2)).check(matches(withText(slot2.getSceneName())));

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 2)).check(matches(withText(slot3.getSceneName())));
>>>>>>> c48e36e233bfa3dd77b24bc30bb1ef28db742b4d
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
