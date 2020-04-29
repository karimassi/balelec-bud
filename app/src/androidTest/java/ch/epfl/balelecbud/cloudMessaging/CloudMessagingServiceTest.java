package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.notifications.NotificationMessageTest;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

public class CloudMessagingServiceTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final CloudMessagingService cloudMessagingService = new CloudMessagingService();
    private final User user = MockDatabaseWrapper.celine;
    private final String token = MockDatabaseWrapper.token1;

    private UiDevice device;

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    mockAuth.signOut();
                    mockAuth.setCurrentUser(user);
                    TokenUtil.setToken(token);
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
    public void onNewTokenTest() {
        cloudMessagingService.onNewToken(token);
        assertThat(TokenUtil.getToken(), is(token));
    }

    @Test
    public void onMessageReceivedTest() {
        cloudMessagingService.onMessageReceived(emptyRemoteMessage());
        device.openNotification();
        assertNull(device.findObject(By.text("title")));
    }

    @Test
    public void sendEmptyMessageTest() {
        JSONObject message = new JSONObject();
        cloudMessagingService.sendMessage(message);
        device.openNotification();
        assertNull(device.findObject(By.text("title")));
    }

    @Test
    public void receivedEmptyMessageTest() {
        cloudMessagingService.receiveMessage(emptyRemoteMessage());
        device.openNotification();
        assertNull(device.findObject(By.text("title")));
    }

    private RemoteMessage emptyRemoteMessage() {
        Map<String, String> message = new HashMap<>();
        return new RemoteMessage.Builder("ID").setData(message)
                .setMessageType(Message.MESSAGE_TYPE_GENERAL).build();
    }
}