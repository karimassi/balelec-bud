package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.schedule.SlotData;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.intents.FlowUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.getItemInSchedule;
import static ch.epfl.balelecbud.testUtils.CustomViewAssertion.switchChecked;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot2;

@RunWith(AndroidJUnit4.class)
public class ScheduleFragmentWithSubscribedConcertTest {

    private MockDatabase mock = MockDatabase.getInstance();

    @Before
    public void setUp() {
        BalelecbudApplication.setAppDatabase(mock);
        SlotData.setIntentLauncher(intent -> { });
        mock.resetDocument(Database.CONCERT_SLOTS_PATH);
        mock.resetDocument(Database.CONCERT_SLOTS_PATH);
        refreshRecyclerView();
        FragmentScenario.launchInContainer(ScheduleFragment.class);
    }

    /** What do do with this ?
    protected Intent addInfoToActivityIntent(Intent intent) {
        FlowUtil.packCallback(new Slot[]{slot1}, intent);
        return intent;
    }**/


    @Test
    public void testUnSubscribeToAConcert() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        SlotData.setIntentLauncher(intent -> {
            if (intent.getAction() == null)
                sync.fail();

            String action = intent.getAction();
            switch (action) {
                case FlowUtil.ACK_CONCERT:
                case FlowUtil.GET_ALL_CONCERT:
                    sync.fail();
                    break;
                case FlowUtil.CANCEL_CONCERT:
                    sync.call();
                case FlowUtil.SUBSCRIBE_CONCERT:
                    break;
            }
        });
        mock.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);
        mock.storeDocument(Database.CONCERT_SLOTS_PATH, slot2);

        refreshRecyclerView();

        onView(getItemInSchedule(0, 3)).perform(click());
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void testSubscribedConcertIsChecked() {
        mock.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);
        mock.storeDocument(Database.CONCERT_SLOTS_PATH, slot2);

        refreshRecyclerView();

        onView(getItemInSchedule(0, 3))
                .check(switchChecked(true));
        onView(getItemInSchedule(1, 3))
                .check(switchChecked(false));
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_schedule)).perform(swipeDown());
    }
}