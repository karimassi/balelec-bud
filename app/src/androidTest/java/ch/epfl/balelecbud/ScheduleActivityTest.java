package ch.epfl.balelecbud;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.notifications.concertFlow.FlowUtil;
import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {

    private MockDatabaseWrapper mock;

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
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3)) ;
        slot3 = new Slot(2, "Upset", "Scène Sat'",  timestamps.get(4), timestamps.get(5));
    }

    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule = new ActivityTestRule<ScheduleActivity>(ScheduleActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = new MockDatabaseWrapper();
            ScheduleAdapter.setDatabaseImplementation(mock);
        }
    };

    @Before
    public void setup() {
        mActivityRule.getActivity().setServiceInterface(new ScheduleAdapter.ServiceInterface() {
            @Override
            public void callService(@NotNull Intent intent) {
                if (intent.getAction() == null)
                    Assert.fail();

                String action = intent.getAction();
                switch (action) {
                    case FlowUtil.ACK_CONCERT:
                    case FlowUtil.GET_ALL_CONCERT:
                        Assert.fail();
                        break;
                    case FlowUtil.SUBSCRIBE_CONCERT:
                    case FlowUtil.CANCEL_CONCERT:
                        break;
                }
            }
        });
    }

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

        mock.modifyItem(slot3, 0);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 2)).check(matches(withText(slot3.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).check(switchChecked(false));

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

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 0)).check(matches(withText(slot1.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1)).check(matches(withText(slot1.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 2)).check(matches(withText(slot1.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).check(switchChecked(false));

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 0)).check(matches(withText(slot2.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1)).check(matches(withText(slot2.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 2)).check(matches(withText(slot2.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).check(switchChecked(false));

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 2), 2)).check(matches(withText(slot3.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).check(switchChecked(false));

    }

    @Test
    public void testCanSubscribeToAConcert() throws Throwable {
        mock.addItem(slot1);

        final List<Object> sync = new LinkedList<>();
        mActivityRule.getActivity().setServiceInterface(new ScheduleAdapter.ServiceInterface() {
            @Override
            public void callService(@NonNull Intent intent) {
                if (intent.getAction() == null)
                    Assert.fail();

                String action = intent.getAction();
                switch (action) {
                    case FlowUtil.ACK_CONCERT:
                    case FlowUtil.CANCEL_CONCERT:
                    case FlowUtil.GET_ALL_CONCERT:
                        Assert.fail();
                        break;
                    case FlowUtil.SUBSCRIBE_CONCERT:
                        Assert.assertEquals(FlowUtil.unpackSlotInIntent(intent), slot1);
                        synchronized (sync) {
                            sync.add(new Object());
                            sync.notify();
                        }
                        break;
                }
            }
        });

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).perform(click());
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertThat(sync.size(), is(1));
    }

    public static ViewAssertion switchChecked(final boolean checked) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null)
                    throw noViewFoundException;
                if (!(view instanceof Switch))
                    throw new AssertionError("The View should be a Switch be was");
                Assert.assertThat(((Switch) view).isChecked(), is(checked));
            }
        };
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
