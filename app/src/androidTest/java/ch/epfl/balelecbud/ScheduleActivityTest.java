package ch.epfl.balelecbud;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import java.util.List;

import ch.epfl.balelecbud.schedule.AbstractScheduleProvider;
import ch.epfl.balelecbud.schedule.ScheduleActivity;
import ch.epfl.balelecbud.schedule.models.Slot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {
    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule = new ActivityTestRule<>(ScheduleActivity.class);

    @Test
    public void testCanGreetUsers() {
        onView(withId(R.id.mainTextEdit)).perform(typeText("from my unit test")).perform(closeSoftKeyboard());
        onView(withId(R.id.mainButton)).perform(click());
        onView(withId(R.id.greetingMessage)).check(matches(withText("Hello from my unit test!")));
    }

    class MockScheduleProvider implements AbstractScheduleProvider {
        @Override
        public void subscribeSlots(RecyclerView.Adapter adapter, List<Slot> slots, List<String> concertIds) {
            Slot slot1 = new Slot("Mr Oizo", "19h - 20h", "Grande scène") ;
            Slot slot2 = new Slot("Walking Furret", "20h - 21h", "Les Azimutes") ;
            Slot slot3 = new Slot("There's no need to be upset", "19h - 20h", "Scène Sat'") ;

            String id1 = "oui";
            String id2 = "ouioui";
            String id3 = "ouiouioui";

            slots.add(slot1);
            slots.add(slot2);
            slots.add(slot3);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);
            slots.add(slot1);

            concertIds.add(id1);
            concertIds.add(id2);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
            concertIds.add(id3);
        }
    }
}
