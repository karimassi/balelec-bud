package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.google.firebase.messaging.RemoteMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CloudMessagingServiceTest {

    private final String title = "This is a generic fun title!";
    private final String body = "This is a fun text :)";
    private final CloudMessagingService cloudMessagingService = new CloudMessagingService();

    private UiDevice device;

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class);

    @Before
    public void setup() {
        cloudMessagingService.setContext(mActivityRule.getActivity());
        device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1_000);
        }
        clearNotifications();
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
    public void onNewTokenCanSetToken() {
        cloudMessagingService.onNewToken(MockDatabaseWrapper.token);
        assertThat(Message.getToken(), is(MockDatabaseWrapper.token));
    }

    @Test
    public void newMessageCanSendNotification() {
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(createMessage())
                .setMessageType(Message.MESSAGE_TYPE_GENERAL).build();
        cloudMessagingService.onMessageReceived(rm);
        verifyNotification();
    }

    @Test
    public void nullDataMessageCantSendNotification() {
        RemoteMessage rm = new RemoteMessage.Builder("ID")
                .setMessageType(Message.MESSAGE_TYPE_GENERAL).build();
        cloudMessagingService.onMessageReceived(rm);
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    private Map<String, String> createMessage() {
        Map<String, String> message = new HashMap<>();
        message.put(Message.DATA_KEY_TITLE, title);
        message.put(Message.DATA_KEY_BODY, body);
        return message;
    }

    private void verifyNotification() {
        device.openNotification();
        assertNotNull(device.wait(Until.hasObject(By.textStartsWith(title)), 30_000));
        UiObject2 titleFound = device.findObject(By.text(title));
        assertNotNull(titleFound);
        assertNotNull(device.findObject(By.text(body)));
    }
}
