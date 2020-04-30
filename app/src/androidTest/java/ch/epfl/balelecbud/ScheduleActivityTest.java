package ch.epfl.balelecbud;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.schedule.SlotData;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.intents.FlowUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.getItemInSchedule;
import static ch.epfl.balelecbud.testUtils.CustomViewAssertion.switchChecked;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot2;

@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest extends BasicActivityTest {
    private final MockDatabase mock = MockDatabase.getInstance();

    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule = new ActivityTestRule<ScheduleActivity>(ScheduleActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppDatabase(mock);
            SlotData.setIntentLauncher(intent -> {
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
            });
        }

        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ScheduleActivity.class);
            FlowUtil.packCallback(new Slot[]{}, intent);
            return intent;
        }
    };

    @Before
    public void setup() {
        mock.resetDocument(Database.CONCERT_SLOTS_PATH);
        refreshRecyclerView();
    }

    @Test
    public void testRecyclerViewVisible() {
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCaseForRecyclerItems() throws Throwable {
        mock.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);
        mock.storeDocument(Database.CONCERT_SLOTS_PATH, slot2);

        refreshRecyclerView();

        checkSlot(0, slot1);
        checkSlot(1, slot2);
    }

    private void checkSlot(int i, Slot slot1) {
        onView(getItemInSchedule(i, 0))
                .check(matches(withText(slot1.getArtistName())));
        onView(getItemInSchedule(i, 1))
                .check(matches(withText(slot1.getTimeSlot())));
        onView(getItemInSchedule(i, 2))
                .check(matches(withText(slot1.getSceneName())));
        onView(getItemInSchedule(i, 3))
                .check(switchChecked(false));
    }

    @Test
    public void testCanSubscribeToAConcert() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        mock.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);

        refreshRecyclerView();

        SlotData.setIntentLauncher(intent -> {
            if (intent.getAction() == null)
                sync.fail();
            String action = intent.getAction();
            switch (action) {
                case FlowUtil.ACK_CONCERT:
                case FlowUtil.CANCEL_CONCERT:
                case FlowUtil.GET_ALL_CONCERT:
                    sync.fail();
                    break;
                case FlowUtil.SUBSCRIBE_CONCERT:
                    sync.assertEquals(slot1, FlowUtil.unpackSlotFromIntent(intent));
                    sync.call();
                    break;
            }
        });

        onView(getItemInSchedule(0, 3))
                .perform(click());
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_schedule)).perform(swipeDown());
    }

    @Override
    protected void setIds() {
        setIds(R.id.schedule_activity_drawer_layout, R.id.schedule_activity_nav_view);
    }
}
