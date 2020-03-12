package ch.epfl.balelecbud;

import android.view.View;
import android.view.ViewGroup;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.balelecbud.schedule.ScheduleActivity;
import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.ScheduleAdapterFacade;
import ch.epfl.balelecbud.schedule.ScheduleDatabase;
import ch.epfl.balelecbud.schedule.SlotListener;
import ch.epfl.balelecbud.schedule.WrappedListener;
import ch.epfl.balelecbud.schedule.models.Slot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;


@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {
    SlotListener listener;

    @Before
    public void setUp(){
        ScheduleDatabase mockDB = new ScheduleDatabase() {
            @Override
            public SlotListener getSlotListener(ScheduleAdapterFacade adapter, List<Slot> slots) {
                listener = new SlotListener(adapter, slots, new WrappedListener() {
                    @Override
                    public void remove() {

                    }

                    @Override
                    public void registerOuterListener(SlotListener outerListener) {

                    }
                });
                return listener;
            }
        };
        ScheduleAdapter.setDatabaseImplementation(mockDB);
        ActivityScenario.launch(ScheduleActivity.class);
    }

    private void addItem(final Slot slot, final String id) throws Throwable{
        Runnable myRunnable = new Runnable(){
            @Override
            public void run() {
                listener.slotAdded(slot, id);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }
    }

    private void modifyItem(final Slot slot, final String id) throws Throwable{
        Runnable myRunnable = new Runnable(){
            @Override
            public void run() {
                listener.slotChanged(slot, id);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }
    }

    private void removeItem(final String id) throws Throwable{
        Runnable myRunnable = new Runnable(){
            @Override
            public void run() {
                listener.slotRemoved(id);
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

        Slot slot1 = new Slot("Mr Oizo", "19h - 20h", "Grande scène") ;
        String id1 = "oui";
        addItem(slot1, id1);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(1)));

        Slot slot2 = new Slot("Walking Furret", "20h - 21h", "Les Azimutes") ;
        String id2 = "ouioui";
        addItem(slot2, id2);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(2)));


        Slot slot3 = new Slot("Upset", "19h - 20h", "Scène Sat'") ;
        String id3 = "ouiouioui";
        addItem(slot3, id3);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(3)));

        modifyItem(slot3, id1);
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 0)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 1)).check(matches(withText("Upset")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 2)).check(matches(withText("Scène Sat'")));

        removeItem(id1);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(2)));

        removeItem(id2);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(1)));

        removeItem(id3);
        onView(withId(R.id.rvSchedule)).check(matches(hasChildCount(0)));
    }

    @Test
    public void testCaseForRecyclerItems() throws Throwable {
        Slot slot1 = new Slot("Mr Oizo", "19h - 20h", "Grande scène") ;
        Slot slot2 = new Slot("Walking Furret", "20h - 21h", "Les Azimutes") ;
        Slot slot3 = new Slot("Upset", "19h - 20h", "Scène Sat'") ;

        String id1 = "oui";
        String id2 = "ouioui";
        String id3 = "ouiouioui";

        addItem(slot1, id1);
        addItem(slot2, id2);
        addItem(slot3, id3);

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 0)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 1)).check(matches(withText("Mr Oizo")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 2)).check(matches(withText("Grande scène")));

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 0)).check(matches(withText("20h - 21h")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 1)).check(matches(withText("Walking Furret")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 1), 2)).check(matches(withText("Les Azimutes")));

        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 0)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 1)).check(matches(withText("Upset")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 2), 2)).check(matches(withText("Scène Sat'")));
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
