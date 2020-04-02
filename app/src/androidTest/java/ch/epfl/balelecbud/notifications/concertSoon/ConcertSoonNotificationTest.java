package ch.epfl.balelecbud.notifications.concertSoon;

import android.app.PendingIntent;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.schedule.models.Slot;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

@RunWith(AndroidJUnit4.class)
public class ConcertSoonNotificationTest {

    //activity does not matter so chose WelcomeActivity
    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void setup() {
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {

            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {

            }
        });
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    @Test
    public void scheduleNotificationWorksCorrectly(){
        String appName = mActivityRule.getActivity().getString(R.string.app_name);
        String expectedTitle = mActivityRule.getActivity().getString(R.string.concert_soon_notification_title);
        String expectedText = "Le nom de mon artiste starts in 15 minutes on Scene 3";
        Context ctx = mActivityRule.getActivity().getApplicationContext();
        Slot s = new Slot(0, "Le nom de mon artiste", "Scene 3", Timestamp.now(), Timestamp.now());

        NotificationScheduler ns = NotificationScheduler.getInstance();
        ns.scheduleNotification(ctx, s);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.textStartsWith(expectedTitle)), 30_000);

        UiObject2 title = device.findObject(By.text(expectedTitle));
        assertEquals(title.getText(), expectedTitle);
        UiObject2 text = device.findObject(By.text(expectedText));
        assertEquals(text.getText(), expectedText);
        title.click();
    }

    @Test
    public void cancelNotificationWorksCorrectly(){
        String appName = mActivityRule.getActivity().getString(R.string.app_name);
        String expectedTitle = mActivityRule.getActivity().getString(R.string.concert_soon_notification_title);

        Context ctx = mActivityRule.getActivity().getApplicationContext();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        // schedule the notification to go off in 5 seconds,
        // which leaves plenty of time to cancel it
        cal.add(Calendar.SECOND, 5);
        cal.add(Calendar.MINUTE, 15);
        Slot s = new Slot(0, "Le nom de mon artiste", "Scene 3", new Timestamp(cal.getTime()), new Timestamp(cal.getTime()));

        NotificationScheduler ns = NotificationScheduler.getInstance();
        ns.scheduleNotification(ctx, s);
        ns.cancelNotification(ctx, s);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(expectedTitle)), 10_000);

        UiObject2 title = device.findObject(By.text(expectedTitle));
        assertNull(title);
        //hide notifications
        device.pressBack();

        device.waitForIdle();
    }
}
