package ch.epfl.balelecbud.view.schedule;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Slot;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.FlowUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.storage.MockStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickChildViewWithId;
import static ch.epfl.balelecbud.utility.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.utility.database.MockDatabase.slot2;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ScheduleFragmentTest {
    private final MockDatabase mockDatabase = MockDatabase.getInstance();
    private final MockStorage mockStorage = MockStorage.getInstance();

    @Before
    public void setup() {
        mockDatabase.resetDatabase();
        mockDatabase.setFreshnessToReturn(0L);
        mockStorage.setAccessCount(0);
        BalelecbudApplication.setAppDatabase(mockDatabase);
        BalelecbudApplication.setAppStorage(mockStorage);

        SlotData.setIntentLauncher(intent -> {
            if (intent.getAction() == null)
                Assert.fail();

            String action = intent.getAction();
            switch (action) {
                case FlowUtils.ACK_CONCERT:
                case FlowUtils.GET_ALL_CONCERT:
                    Assert.fail();
                    break;
                case FlowUtils.SUBSCRIBE_CONCERT:
                case FlowUtils.CANCEL_CONCERT:
                    break;
            }
        });
        mockDatabase.resetDocument(Database.CONCERT_SLOTS_PATH);
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList("slots", new ArrayList<>());
        FragmentScenario.launchInContainer(ScheduleFragment.class, arguments);
    }

    @Test
    public void testRecyclerViewVisible() {
        onView(ViewMatchers.withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testItemModification() {
        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(0)));

        mockDatabase.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);

        refreshRecyclerView();

        onView(withId(R.id.scheduleRecyclerView)).check(matches(hasChildCount(1)));

    }

    @Test
    public void testCaseForRecyclerItems() {

        mockDatabase.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);
        mockDatabase.storeDocument(Database.CONCERT_SLOTS_PATH, slot2);

        refreshRecyclerView();

        checkSlot(0, slot1);
        checkSlot(1, slot2);

        assertEquals(mockStorage.getAccessCount(), 2);
    }

    private void checkSlot(int i, Slot slot1) {
        RecyclerViewMatcher matcher = new RecyclerViewMatcher(R.id.scheduleRecyclerView);
        onView(matcher.atPosition(i)).check(matches(hasDescendant(withText(slot1.getArtistName()))));
        onView(matcher.atPosition(i)).check(matches(hasDescendant(withText(slot1.getTimeSlot()))));
        onView(matcher.atPosition(i)).check(matches(hasDescendant(withText(slot1.getSceneName()))));
        onView(matcher.atPosition(i)).check(matches(hasDescendant(withId(R.id.ScheduleSubscribeSwitch))));
    }

    @Test
    public void testCanSubscribeToAConcert() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        mockDatabase.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);

        refreshRecyclerView();

        SlotData.setIntentLauncher(intent -> {
            if (intent.getAction() == null)
                sync.fail();
            String action = intent.getAction();
            switch (action) {
                case FlowUtils.ACK_CONCERT:
                case FlowUtils.CANCEL_CONCERT:
                case FlowUtils.GET_ALL_CONCERT:
                    sync.fail();
                    break;
                case FlowUtils.SUBSCRIBE_CONCERT:
                    sync.assertEquals(slot1, FlowUtils.unpackSlotFromIntent(intent));
                    sync.call();
                    break;
            }
        });

        onView(withId(R.id.scheduleRecyclerView)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, clickChildViewWithId(R.id.ScheduleSubscribeSwitch)));
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_schedule)).perform(swipeDown());
    }
}
