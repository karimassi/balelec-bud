package ch.epfl.balelecbud.notifications;

import android.app.PendingIntent;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.google.android.gms.location.LocationRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.cloudMessaging.Message;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class NotificationMessageTest {

    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    LocationUtil.setLocationClient(new LocationClient() {
                        @Override
                        public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                        }

                        @Override
                        public void removeLocationUpdates(PendingIntent intent) {
                        }
                    });
                    BalelecbudApplication.setAppDatabaseWrapper(mock);
                }
            };
    private UiDevice device;

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1_000);
        }
        // quit the notifications center if it happens to be open
        clearNotifications();

        mock.resetDocument(DatabaseWrapper.CONCERT_SLOTS_PATH);
    }

    @After
    public void tearDown() {
        clearNotifications();
    }

    private void clearNotifications() {
        device.openNotification();
        UiObject2 button = device.findObject(By.text("CLEAR ALL"));
        if (button != null) button.click();
        device.pressBack();
    }

    @Test
    public void canScheduleNotification() {
        String title = "This is a generic fun title!";
        String body = "This is a fun text :)";

        Map<String, String> message = new HashMap<>();
        message.put(Message.DATA_KEY_TITLE, title);
        message.put(Message.DATA_KEY_BODY, body);
        message.put(Message.DATA_KEY_TYPE, Message.MESSAGE_TYPE_GENERAL);

        Context context = mActivityRule.getActivity();

        NotificationMessage.getInstance().scheduleNotification(context, message);

        device.openNotification();
        assertNotNull(device.wait(Until.hasObject(By.textStartsWith(title)), 30_000));
        UiObject2 titleFound = device.findObject(By.text(title));
        assertNotNull(titleFound);
        assertNotNull(device.findObject(By.text(body)));
    }
}