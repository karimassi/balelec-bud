package ch.epfl.balelecbud.cloudMessaging;

import android.content.Context;

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

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.notifications.NotificationMessage;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.util.http.HttpClient;
import ch.epfl.balelecbud.util.http.MockHttpClient;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CloudMessagingServiceTest {

    public static final CloudMessagingService cloudMessagingService = new CloudMessagingService();

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final HttpClient mockHttpClient = MockHttpClient.getInstance();
    private final User user = MockDatabaseWrapper.celine;
    private final String token = MockDatabaseWrapper.token;
    private final String title = "This is a generic fun title!";
    private final String body = "This is a fun text :)";

    private UiDevice device;

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppDatabaseWrapper(mockDB);
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    BalelecbudApplication.setHttpClient(mockHttpClient);
                    mockAuth.signOut();
                    mockAuth.setCurrentUser(user);
                    Message.setToken(token);
                }
            };

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

    @Test
    public void onNewTokenTest() {
        cloudMessagingService.onNewToken(MockDatabaseWrapper.token);
        assertThat(Message.getToken(), is(MockDatabaseWrapper.token));
    }

    @Test
    public void onMessageReceivedTest() {
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(createMessage())
                .setMessageType(Message.MESSAGE_TYPE_GENERAL).build();
        cloudMessagingService.onMessageReceived(rm);
        verifyNotification();
        NotificationMessage.getInstance().cancelNotification(mActivityRule.getActivity(), createMessage());
    }

    @Test
    public void sendMessageTest() {
        Message message = new Message(Message.MESSAGE_TYPE_GENERAL, title, body);
        message.sendMessage(user.getUid());
        verifyNotification();
    }

    @Test
    public void cantSendMessageToUserWithoutToken() {
        Message message = new Message(Message.MESSAGE_TYPE_GENERAL, title, body);
        message.sendMessage(MockDatabaseWrapper.karim.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void cantSendNullMessage() {
        Message message = new Message(Message.MESSAGE_TYPE_GENERAL, null, null);
        message.sendMessage(user.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void cantNotifyNullData() {
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

    private void clearNotifications() {
        device.openNotification();
        UiObject2 button = device.findObject(By.text("CLEAR ALL"));
        if (button != null) button.click();
        device.pressBack();
    }
}
