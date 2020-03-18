package ch.epfl.balelecbud;

import android.view.View;
import android.view.ViewGroup;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.Timestamp;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
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
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

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

    @Before
    public void setUp(){
        mock = new MockDatabaseWrapper();
        ScheduleAdapter.setDatabaseImplementation(mock);
        ActivityScenario.launch(ScheduleActivity.class);
    }

    private void addItem(final Slot slot) throws Throwable{
        Runnable myRunnable = new Runnable(){
            @Override
            public void run() {
                mock.addItem(slot);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }
    }

    private void modifyItem(final Slot slot, final int index) throws Throwable{
        Runnable myRunnable = new Runnable(){
            @Override
            public void run() {
                mock.changeItem(slot, index);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }
    }

    private void removeItem(final Slot slot, final int index) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mock.removeItem(slot, index);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }
    }

    @Test
    public void testRecyclerViewVisible() {
        onView(withId(R.id.rvSchedule)).check(matches(isDisplayed()));
    }

    @Test
    public void testItemModification() throws Throwable{

        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(0)));

        addItem(slot1);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(1)));

        addItem(slot2);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(2)));

        addItem(slot3);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(3)));

        modifyItem(slot3, 0);
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 2)).check(matches(withText(slot3.getSceneName())));

        removeItem(slot3, 0);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(2)));

        removeItem(slot2, 0);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(1)));

        removeItem(slot3, 0);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(0)));
    }

    @Test
    public void testCaseForRecyclerItems() throws Throwable {

        addItem(slot1);
        addItem(slot2);
        addItem(slot3);

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 0)).check(matches(withText(slot1.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 1)).check(matches(withText(slot1.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 2)).check(matches(withText(slot1.getSceneName())));

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 0)).check(matches(withText(slot2.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 1)).check(matches(withText(slot2.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 2)).check(matches(withText(slot2.getSceneName())));

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 2)).check(matches(withText(slot3.getSceneName())));
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
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
