package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.balelecbud.gallery.GalleryFragment;
import ch.epfl.balelecbud.schedule.SlotData;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.intents.FlowUtil;
import ch.epfl.balelecbud.util.storage.MockStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.getItemInSchedule;
import static ch.epfl.balelecbud.testUtils.CustomViewAssertion.switchChecked;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot2;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GalleryFragmentTest {

    private final MockStorage mockStorage = new MockStorage();

    @Before
    public void setup() {
        mockStorage.setAccessCount(0);
        BalelecbudApplication.setAppStorage(mockStorage);
        FragmentScenario.launchInContainer(GalleryFragment.class);
    }

    @Test
    public void testRecyclerViewVisible() {
        onView(withId(R.id.galleryRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testItemModification() {
        onView(withId(R.id.galleryRecyclerView)).check(matches(hasChildCount(0)));

        //mockDatabase.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);

        refreshRecyclerView();

        onView(withId(R.id.galleryRecyclerView)).check(matches(hasChildCount(1)));

        //mockDatabase.storeDocument(Database.CONCERT_SLOTS_PATH, slot2);

        refreshRecyclerView();


        onView(withId(R.id.galleryRecyclerView)).check(matches(hasChildCount(2)));
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
        mockDatabase.storeDocument(Database.CONCERT_SLOTS_PATH, slot1);

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
}
