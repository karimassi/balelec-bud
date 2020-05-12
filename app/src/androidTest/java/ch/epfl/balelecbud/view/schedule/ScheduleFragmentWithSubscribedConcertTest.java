package ch.epfl.balelecbud.view.schedule;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.FlowUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.getItemInSchedule;
import static ch.epfl.balelecbud.testUtils.CustomViewAssertion.switchChecked;
import static ch.epfl.balelecbud.utility.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.utility.database.MockDatabase.slot2;

@RunWith(AndroidJUnit4.class)
public class ScheduleFragmentWithSubscribedConcertTest {

    private MockDatabase mock = MockDatabase.getInstance();

    @Before
    public void setUp() {
        mock.resetDatabase();
        BalelecbudApplication.setAppDatabase(mock);
        SlotData.setIntentLauncher(intent -> {});
        mock.resetDocument(Database.CONCERT_SLOTS_PATH);
        mock.resetDocument(Database.CONCERT_SLOTS_PATH);
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList("slots", Lists.newArrayList(slot1));
        FragmentScenario.launchInContainer(ScheduleFragment.class, arguments);
    }


    @Test
    public void testUnSubscribeToAConcert() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        SlotData.setIntentLauncher(intent -> {
            if (intent.getAction() == null)
                sync.fail();

            String action = intent.getAction();
            switch (action) {
                case FlowUtils.ACK_CONCERT:
                case FlowUtils.GET_ALL_CONCERT:
                    sync.fail();
                    break;
                case FlowUtils.CANCEL_CONCERT:
                    sync.call();
                case FlowUtils.SUBSCRIBE_CONCERT:
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
        onView(ViewMatchers.withId(R.id.swipe_refresh_layout_schedule)).perform(swipeDown());
    }
}