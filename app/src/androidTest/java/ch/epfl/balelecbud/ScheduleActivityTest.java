package ch.epfl.balelecbud;

import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import java.util.List;

import ch.epfl.balelecbud.schedule.AbstractScheduleProvider;
import ch.epfl.balelecbud.schedule.ScheduleActivity;
import ch.epfl.balelecbud.schedule.models.Slot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {
    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule = new ActivityTestRule<>(ScheduleActivity.class);

    @Test
    public void testCanGreetUsers() {
        onView(withId(R.id.rvSchedule)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("example text")), click()))
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
        }
    }

    public static Matcher<View> withViewAtPosition(final int position, final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }


}
