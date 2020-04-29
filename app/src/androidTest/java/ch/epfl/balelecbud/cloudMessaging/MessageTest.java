package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

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
import ch.epfl.balelecbud.notifications.NotificationMessageTest;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MessageTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final MockMessagingService mockMessagingService = MockMessagingService.getInstance();
    private final User user = MockDatabaseWrapper.celine;
    private final User friend = MockDatabaseWrapper.karim;
    private final String token = MockDatabaseWrapper.token1;
    private final String title = "This title is the best!";
    private final String body = "This body is good :)";

    private UiDevice device;

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppDatabaseWrapper(mockDB);
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    BalelecbudApplication.setAppMessagingService(mockMessagingService);
                    mockAuth.signOut();
                    mockAuth.setCurrentUser(user);
                    TokenUtil.setToken(token);
                }
            };

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        NotificationMessageTest.clearNotifications(device);
        mockMessagingService.setContext(mActivityRule.getActivity());
    }

    @After
    public void tearDown() {
        NotificationMessageTest.clearNotifications(device);
    }

    @Test
    public void sendFriendRequestMessageTest() {
        Message.sendFriendRequestMessage(friend, user.getUid());
        NotificationMessageTest.verifyNotification(device, Message.FRIEND_REQUEST_TITLE,
                friend.getDisplayName() + Message.FRIEND_REQUEST_BODY);
    }

    @Test
    public void sendFriendRequestMessageWithoutToken() {
        Message.sendFriendRequestMessage(MockDatabaseWrapper.axel, user.getUid());
        assertNull(device.findObject(By.text(Message.FRIEND_REQUEST_TITLE)));
    }

    @Test
    public void sendAcceptRequestMessageTest() {
        Message.sendAcceptRequestMessage(friend, user.getUid());
        NotificationMessageTest.verifyNotification(device, Message.ACCEPT_REQUEST_TITLE,
                friend.getDisplayName() + Message.ACCEPT_REQUEST_BODY);
    }

    @Test
    public void sendAcceptRequestMessageWithoutToken() {
        Message.sendAcceptRequestMessage(MockDatabaseWrapper.axel, user.getUid());
        assertNull(device.findObject(By.text(Message.ACCEPT_REQUEST_TITLE)));
    }

    @Test
    public void sendMessageToUserWithToken() {
        Message message = new Message(title, body, Message.MESSAGE_TYPE_GENERAL);
        message.sendMessage(user.getUid());
        NotificationMessageTest.verifyNotification(device, title, body);
    }

    @Test
    public void sendMessageToUserWithoutToken() {
        Message message = new Message(title, body, Message.MESSAGE_TYPE_GENERAL);
        message.sendMessage(MockDatabaseWrapper.axel.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void sendNullMessageTest() {
        Message message = new Message(null, null, Message.MESSAGE_TYPE_GENERAL);
        message.sendMessage(user.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void extractMessageTest() {
        Map<String, String> message = new HashMap<>();
        message.put(Message.DATA_KEY_TITLE, title);
        message.put(Message.DATA_KEY_BODY, body);
        message.put(Message.DATA_KEY_TYPE, Message.MESSAGE_TYPE_GENERAL);
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message).build();
        Map<String, String> result = Message.extractMessage(rm);
        assertThat(result, is(message));
    }

    @Test
    public void extractEmptyMessageTest() {
        Map<String, String> message = new HashMap<>();
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message).build();
        Map<String, String> result = Message.extractMessage(rm);
        assertTrue(result.isEmpty());
    }
}
