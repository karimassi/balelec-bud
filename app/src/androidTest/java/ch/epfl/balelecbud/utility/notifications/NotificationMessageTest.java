package ch.epfl.balelecbud.utility.notifications;

import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.cloudMessaging.Message;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppAuthenticator;
import static ch.epfl.balelecbud.utility.database.MockDatabase.celine;
import static org.junit.Assert.assertNotNull;

public class NotificationMessageTest {

    private static final String title = "This is the title in NotificationMessageTest";
    private static final String body = "You can imagine what this is...";

    private UiDevice device;

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    setAppAuthenticator(MockAuthenticator.getInstance());
                    MockAuthenticator.getInstance().setCurrentUser(celine);
                }
            };

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        NotificationMessageTest.clearNotifications(device);
    }

    @After
    public void tearDown() {
        NotificationMessageTest.clearNotifications(device);
    }

    @Test
    public void scheduleGeneralNotificationTest() {
        scheduleNotificationTest(getAppContext().getString(R.string.message_type_general));
    }

    @Test
    public void scheduleSocialNotificationTest() {
        scheduleNotificationTest(getAppContext().getString(R.string.message_type_social));
    }

    private void scheduleNotificationTest(String type) {
        Map<String, String> message = Message.createMessage(title, body, type);
        NotificationMessage.getInstance().scheduleNotification(mActivityRule.getActivity(), message);
        verifyNotification(device, title, body);
    }

    public static void verifyNotification(UiDevice device, String title, String body) {
        assertNotNull(device.wait(Until.hasObject(By.textStartsWith(title)), 30_000));
        assertNotNull(device.findObject(By.text(title)));
        assertNotNull(device.findObject(By.text(body)));
        clearNotifications(device);
    }

    public static void clearNotifications(UiDevice device) {
        device.openNotification();
        UiObject2 button = device.findObject(By.text("CLEAR ALL"));
        if (button != null) {
            button.click();
        } else {
            device.pressBack();
        }
    }
}