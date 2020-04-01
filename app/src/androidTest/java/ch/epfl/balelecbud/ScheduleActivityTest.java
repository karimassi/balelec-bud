package ch.epfl.balelecbud;

<<<<<<< HEAD
import android.view.Gravity;
=======
import android.content.Intent;
>>>>>>> master
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

<<<<<<< HEAD
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
=======
import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
>>>>>>> master
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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

import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.util.intents.FlowUtil;
import ch.epfl.balelecbud.util.intents.IntentLauncher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
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
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
            ScheduleAdapter.setDatabaseImplementation(mock);
        }
    };

    @Before
    public void setup() {
        mActivityRule.getActivity().setIntentLauncher(new IntentLauncher() {
            @Override
            public void launchIntent(@NonNull Intent intent) {
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

<<<<<<< HEAD
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 0)).check(matches(withText("Mr Oizo")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 1)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 2)).check(matches(withText("Grande scène")));

        mock.modifyItem(slot2, 0);
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 0)).check(matches(withText("Walking Furret")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 1)).check(matches(withText("20h - 21h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 2)).check(matches(withText("Les Azimutes")));
=======
        mock.addItem(slot3);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(3)));

        mock.modifyItem(slot3, 0);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 0)).check(matches(withText(slot3.getTimeSlot())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1)).check(matches(withText(slot3.getArtistName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 2)).check(matches(withText(slot3.getSceneName())));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).check(switchChecked(false));

        mock.removeItem(slot3, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(2)));
>>>>>>> master

        mock.removeItem(slot2, 0);
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(1)));
    }

    @Test
    public void testCaseForRecyclerItems() throws Throwable {
<<<<<<< HEAD
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Slot slot2 = new Slot("Walking Furret", "Les Azimutes", "20h - 21h");
=======
>>>>>>> master

        mock.addItem(slot1);
        mock.addItem(slot2);

<<<<<<< HEAD
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 0)).check(matches(withText("Mr Oizo")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 1)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 2)).check(matches(withText("Grande scène")));

        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1), 0)).check(matches(withText("Walking Furret")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1), 1)).check(matches(withText("20h - 21h")));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1), 2)).check(matches(withText("Les Azimutes")));
=======
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
        mActivityRule.getActivity().setIntentLauncher(new IntentLauncher() {
            @Override
            public void launchIntent(@NonNull Intent intent) {
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
                        Assert.assertEquals(FlowUtil.unpackSlotFromIntent(intent), slot1);
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
>>>>>>> master
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
