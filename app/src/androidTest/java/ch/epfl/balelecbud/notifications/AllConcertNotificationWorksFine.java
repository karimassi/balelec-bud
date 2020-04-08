package ch.epfl.balelecbud.notifications;

import android.app.PendingIntent;

import androidx.room.Room;
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
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.notifications.concertFlow.objects.ConcertOfInterestDatabase;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.nthChildOf;
import static ch.epfl.balelecbud.testUtils.CustomViewAssertion.switchChecked;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AllConcertNotificationWorksFine {
    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            LocationUtil.setLocationClient(new LocationClient() {
                @Override
                public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) { }

                @Override
                public void removeLocationUpdates(PendingIntent intent) { }
            });
            BalelecbudApplication.setAppDatabaseWrapper(mock);
        }
    };

    private ConcertOfInterestDatabase db;
    private UiDevice device;
    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();
    private final Slot s = new Slot(0, "Le nom de mon artiste", "Scene 3",
            Timestamp.now(), Timestamp.now());

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1_000);
        }
        this.db = Room.inMemoryDatabaseBuilder(
                getApplicationContext(),
                ConcertOfInterestDatabase.class
        ).build();
        ConcertFlow.setMockDb(db);
    }

    @After
    public void tearDown() {
        assertTrue(this.db.isOpen());
        this.db.close();
        clearNotifications();
    }

    private void clearNotifications() {
        device.openNotification();
        UiObject2 button = device.findObject(By.text("CLEAR ALL"));
        if (button != null) {
            button.click();
        } else {
            device.pressBack();
        }
    }

    @Test
    public void subscribeToAConcertScheduleANotification() throws Throwable {
        checkSwitchAfter(this::checkNotification, s, false);
    }

    @Test
    public void subscribeToAConcertKeepItSubscribed() throws Throwable {
        checkSwitchAfter(device::pressBack, s, true);
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
        Slot s1 = new Slot(0, "Le nom de mon artiste", "Scene 3",
                new Timestamp(cal.getTime()), new Timestamp(cal.getTime()));


        onView(withId(R.id.scheduleButton)).perform(click());
        mock.addItem(s1);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3))
                .perform(click());
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3))
                .perform(click());

        device.openNotification();
        device.wait(Until.hasObject(By.text(expectedTitle)), 10_000);

        assertFalse(device.hasObject(By.text(expectedTitle)));
        //hide notifications
        device.pressBack();

        device.waitForIdle();
    }

    private void checkSwitchAfter(Runnable runnable, Slot s,  boolean switchStateAfter) throws Throwable {
        onView(withId(R.id.scheduleButton)).perform(click());
        mock.addItem(s);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3))
                .perform(click());
        runnable.run();
        device.waitForIdle();
        onView(withId(R.id.scheduleButton)).perform(click());
        mock.addItem(s);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3))
                .check(switchChecked(switchStateAfter));
    }

    private void checkNotification() {
        String expectedTitle = mActivityRule.getActivity().getString(R.string.concert_soon_notification_title);
        String expectedText = "Le nom de mon artiste starts in 15 minutes on Scene 3";

        device.openNotification();
        assertNotNull(device.wait(Until.hasObject(By.textStartsWith(expectedTitle)), 30_000));
        UiObject2 title = device.findObject(By.text(expectedTitle));
        assertNotNull(title);
        assertNotNull(device.findObject(By.text(expectedText)));
        title.click();
    }
}
