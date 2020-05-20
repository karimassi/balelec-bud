package ch.epfl.balelecbud.utility.notifications;

import android.app.PendingIntent;
import android.util.Log;
import android.view.Gravity;

import androidx.room.Room;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.Timestamp;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.model.Slot;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.location.LocationClient;
import ch.epfl.balelecbud.utility.location.LocationUtils;
import ch.epfl.balelecbud.utility.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.utility.notifications.concertFlow.objects.ConcertOfInterestDatabase;
import ch.epfl.balelecbud.view.schedule.SlotData;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.getItemInSchedule;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.nthChildOf;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickChildViewWithId;
import static ch.epfl.balelecbud.testUtils.CustomViewAssertion.switchChecked;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ConcertNotificationTest {

    private final MockDatabase mock = MockDatabase.getInstance();
    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    MockDatabase.getInstance().resetDatabase();
                    LocationUtils.setLocationClient(new LocationClient() {
                        @Override
                        public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                        }

                        @Override
                        public void removeLocationUpdates(PendingIntent intent) {
                        }
                    });
                    BalelecbudApplication.setAppDatabase(mock);
                }
            };
    private final Slot s = new Slot(0, "Le nom de mon artiste", "Scene 3", "path1",
            Timestamp.now(), Timestamp.now());
    private ConcertOfInterestDatabase db;
    private UiDevice device;

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1_000);
        }
        // quit the notifications center if it happens to be open
        NotificationMessageTest.clearNotifications(device);

        mock.resetDocument(Database.CONCERT_SLOTS_PATH);
        SlotData.setIntentLauncher(null);
        this.db = Room.inMemoryDatabaseBuilder(
                getApplicationContext(),
                ConcertOfInterestDatabase.class
        ).build();
        ConcertFlow.setMockDb(db);
    }

    @After
    public void tearDown() {
        this.db.close();
        NotificationMessageTest.clearNotifications(device);
    }

    @Test
    public void subscribeToAConcertScheduleANotification() throws Throwable {
        checkSwitchAfter(() -> {
            checkNotification();
            device.waitForWindowUpdate(null, 10000);
            openScheduleActivityFrom(R.id.root_activity_drawer_layout, R.id.root_activity_nav_view);
            Log.v("mySuperTag", "executed subscribeToAConcertScheduleANotification");
        }, s, false);
    }

    @Test
    public void subscribeToAConcertKeepItSubscribed() throws Throwable {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        // schedule the notification to go off in 45 minutes,
        // which leaves plenty of time for the test to finish and cancel it
        cal.add(Calendar.MINUTE, 60);
        Slot s1 = new Slot(0, "Le nom de mon artiste", "Scene 3", "path1",
                new Timestamp(cal.getTime()), new Timestamp(cal.getTime()));
        checkSwitchAfter(() -> {
            openInfoActivityFrom(R.id.root_activity_drawer_layout, R.id.root_activity_nav_view);
            openScheduleActivityFrom(R.id.root_activity_drawer_layout, R.id.root_activity_nav_view);
            Log.v("mySuperTag", "executed subscribeToAConcertKeepItSubscribed");
        }, s1, true);
        onView(withId(R.id.scheduleRecyclerView)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, clickChildViewWithId(R.id.ScheduleSubscribeSwitch)));
    }

    @Test
    public void unsubscribeToAConcertCancelNotification() throws Throwable {
        String expectedTitle = getApplicationContext().getString(R.string.concert_soon_notification_title);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        // schedule the notification to go off in 5 seconds,
        // which leaves plenty of time to cancel it
        cal.add(Calendar.SECOND, 5);
        cal.add(Calendar.MINUTE, 15);
        Slot s1 = new Slot(0, "Le nom de mon artiste", "Scene 3", "path1",
                new Timestamp(cal.getTime()), new Timestamp(cal.getTime()));

        mock.storeDocument(Database.CONCERT_SLOTS_PATH, s1);

        openScheduleActivityFrom(R.id.root_activity_drawer_layout, R.id.root_activity_nav_view);

        assertNotNull(device.wait(Until.hasObject(By.text(s1.getArtistName())), 1000));
        onView(getItemInSchedule(0, 2, 2)).perform(click());
        onView(getItemInSchedule(0, 2, 2)).perform(click());

        device.openNotification();
        device.wait(Until.hasObject(By.text(expectedTitle)), 10_000);

        assertFalse(device.hasObject(By.text(expectedTitle)));
        //hide notifications
        device.pressBack();

        device.waitForIdle();
    }

    private void checkSwitchAfter(Runnable runnable, Slot s, boolean switchStateAfter) throws Throwable {

        mock.storeDocument(Database.CONCERT_SLOTS_PATH, s);

        openScheduleActivityFrom(R.id.root_activity_drawer_layout, R.id.root_activity_nav_view);

        onView(getItemInSchedule(0, 2, 2)).perform(click());

        runnable.run();

        refreshRecyclerView();

        onView(getItemInSchedule(0, 2, 2))
                .check(switchChecked(switchStateAfter));
    }

    private void checkNotification() {
        String expectedTitle = mActivityRule.getActivity().getString(R.string.concert_soon_notification_title);
        String expectedText = "Le nom de mon artiste starts in 15 minutes on Scene 3";

        UiObject2 title = NotificationMessageTest.verifyNotification(device, expectedTitle, expectedText);

        title.click();
    }

    private void openDrawerFrom(int layout_id, int nav_id) {
        onView(withId(layout_id)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(nav_id)).check(matches(isDisplayed()));
    }

    private void clickItemFrom(int itemId, int nav_id) {
        onView(withId(nav_id)).perform(NavigationViewActions.navigateTo(itemId));
    }

    private void openScheduleActivityFrom(int layout_id, int nav_id) {
        openDrawerFrom(layout_id, nav_id);
        clickItemFrom(R.id.activity_main_drawer_schedule, nav_id);
        device.waitForIdle(10000);
    }

    private void openInfoActivityFrom(int layout_id, int nav_id) {
        openDrawerFrom(layout_id, nav_id);
        clickItemFrom(R.id.activity_main_drawer_info, nav_id);
        device.waitForIdle(10000);
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_schedule)).perform(swipeDown());
    }
}