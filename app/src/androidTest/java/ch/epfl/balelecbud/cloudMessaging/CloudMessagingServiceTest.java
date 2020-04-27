package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

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
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

public class CloudMessagingServiceTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final CloudMessagingService cloudMessagingService = CloudMessagingService.getInstance();
    private final User user = MockDatabaseWrapper.celine;
    private final String token = MockDatabaseWrapper.token;

    private UiDevice device;

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        clearNotifications();
    }

    @After
    public void tearDown() {
        clearNotifications();
    }

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

    @Test
    public void onNewTokenTest() {
        cloudMessagingService.onNewToken(token);
        assertThat(TokenUtil.getToken(), is(token));
    }

    @Test
    public void receivedEmptyMessageTest() {
        Map<String, String> message = new HashMap<>();
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message)
                .setMessageType(Message.MESSAGE_TYPE_GENERAL).build();
        cloudMessagingService.onMessageReceived(rm);
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

    private void clearNotifications() {
        device.openNotification();
        UiObject2 button = device.findObject(By.text("CLEAR ALL"));
        if (button != null) button.click();
        device.pressBack();
    }
}