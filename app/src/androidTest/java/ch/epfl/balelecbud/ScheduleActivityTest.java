package ch.epfl.balelecbud;

import android.content.res.Resources;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.balelecbud.schedule.AbstractScheduleProvider;
import ch.epfl.balelecbud.schedule.ScheduleActivity;
import ch.epfl.balelecbud.schedule.models.Slot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {
    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule = new ActivityTestRule<>(ScheduleActivity.class);

    @Test
    public void testCanGreetUsers() {
        //onView(withId(R.id.itemlayout)).check(matches(atPosition(0, hasDescendant(withText("19h - 20h")))));
        onView(withRecyclerView(R.id.rvSchedule)
                .atPositionOnView(1, R.id.tim))
                .check(matches(withText("Test text")));
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

    public class RecyclerViewMatcher {
        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)",
                                    new Object[] { Integer.valueOf
                                            (recyclerViewId) });
                        }
                    }

                    description.appendText("with id: " + idDescription);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView =
                                (RecyclerView) view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                        }
                        else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }

                }
            };
        }
    }
}
