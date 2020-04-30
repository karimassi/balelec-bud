package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.core.app.ApplicationProvider;
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
import static ch.epfl.balelecbud.cloudMessaging.Message.ACCEPT_REQUEST_BODY;
import static ch.epfl.balelecbud.cloudMessaging.Message.ACCEPT_REQUEST_TITLE;
import static ch.epfl.balelecbud.cloudMessaging.Message.DATA_KEY_BODY;
import static ch.epfl.balelecbud.cloudMessaging.Message.DATA_KEY_TITLE;
import static ch.epfl.balelecbud.cloudMessaging.Message.DATA_KEY_TYPE;
import static ch.epfl.balelecbud.cloudMessaging.Message.FRIEND_REQUEST_BODY;
import static ch.epfl.balelecbud.cloudMessaging.Message.FRIEND_REQUEST_TITLE;
import static ch.epfl.balelecbud.cloudMessaging.Message.MESSAGE_TYPE_GENERAL;
import static ch.epfl.balelecbud.cloudMessaging.Message.TYPE_ACCEPT_REQUEST;
import static ch.epfl.balelecbud.cloudMessaging.Message.TYPE_FRIEND_REQUEST;
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
                    BalelecbudApplication.setAppContext(ApplicationProvider.getApplicationContext());
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
    public void sendFriendshipMessageNullType() {
        Message.sendFriendshipMessage(friend, user.getUid(), null);
        assertNull(device.findObject(By.text(ACCEPT_REQUEST_TITLE)));
        assertNull(device.findObject(By.text(FRIEND_REQUEST_TITLE)));
    }

    @Test
    public void sendFriendRequestMessageTest() {
        sendFriendshipMessageTest(TYPE_FRIEND_REQUEST, FRIEND_REQUEST_TITLE, FRIEND_REQUEST_BODY);
    }

    @Test
    public void sendFriendRequestMessageWithoutToken() {
        Message.sendFriendshipMessage(MockDatabaseWrapper.axel, user.getUid(), TYPE_FRIEND_REQUEST);
        assertNull(device.findObject(By.text(FRIEND_REQUEST_TITLE)));
    }

    @Test
    public void sendAcceptRequestMessageTest() {
        sendFriendshipMessageTest(TYPE_ACCEPT_REQUEST, ACCEPT_REQUEST_TITLE, ACCEPT_REQUEST_BODY);
    }

    @Test
    public void sendAcceptRequestMessageWithoutToken() {
        Message.sendFriendshipMessage(MockDatabaseWrapper.axel, user.getUid(), TYPE_ACCEPT_REQUEST);
        assertNull(device.findObject(By.text(ACCEPT_REQUEST_TITLE)));
    }

    @Test
    public void sendMessageToUserWithToken() {
        Message message = new Message(title, body, MESSAGE_TYPE_GENERAL);
        message.sendMessage(user.getUid());
        NotificationMessageTest.verifyNotification(device, title, body);
    }

    @Test
    public void sendMessageToUserWithoutToken() {
        Message message = new Message(title, body, MESSAGE_TYPE_GENERAL);
        message.sendMessage(MockDatabaseWrapper.axel.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void sendNullMessageTest() {
        Message message = new Message(null, null, MESSAGE_TYPE_GENERAL);
        message.sendMessage(user.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void extractMessageTest() {
        Map<String, String> message = new HashMap<>();
        message.put(DATA_KEY_TITLE, title);
        message.put(DATA_KEY_BODY, body);
        message.put(DATA_KEY_TYPE, MESSAGE_TYPE_GENERAL);
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

    private void sendFriendshipMessageTest(String type, String title, String body) {
        Message.sendFriendshipMessage(friend, user.getUid(), type);
        NotificationMessageTest.verifyNotification(device, title,
                friend.getDisplayName() + body);
    }
}
