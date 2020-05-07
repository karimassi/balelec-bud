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
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.notifications.NotificationMessageTest;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MessageTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final MockMessagingService mockMessagingService = MockMessagingService.getInstance();
    private final User user = MockDatabase.celine;
    private final String token = MockDatabase.token1;
    private final User friend = MockDatabase.karim;
    private final String title = "This title is the best!";
    private final String body = "This body is good :)";

    private UiDevice device;

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    mockDB.resetDatabase();
                    BalelecbudApplication.setAppDatabase(mockDB);
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
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.accept_request_title))));
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.friend_request_title))));
    }

    @Test
    public void sendFriendRequestMessageTest() {
        sendFriendshipMessageTest(getAppContext().getString(R.string.type_friend_request),
                getAppContext().getString(R.string.friend_request_title),
                getAppContext().getString(R.string.friend_request_body));
    }

    @Test
    public void sendFriendRequestMessageWithoutToken() {
        Message.sendFriendshipMessage(MockDatabase.axel, user.getUid(),
                getAppContext().getString(R.string.type_friend_request));
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.friend_request_title))));
    }

    @Test
    public void sendAcceptRequestMessageTest() {
        sendFriendshipMessageTest(getAppContext().getString(R.string.type_accept_request),
                getAppContext().getString(R.string.accept_request_title),
                getAppContext().getString(R.string.accept_request_body));
    }

    @Test
    public void sendAcceptRequestMessageWithoutToken() {
        Message.sendFriendshipMessage(MockDatabase.axel, user.getUid(),
                getAppContext().getString(R.string.type_accept_request));
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.accept_request_title))));
    }

    @Test
    public void sendMessageToUserWithToken() {
        Message message = new Message(title, body, getAppContext().getString(R.string.message_type_general));
        message.sendMessage(user.getUid());
        NotificationMessageTest.verifyNotification(device, title, body);
    }

    @Test
    public void sendMessageToUserWithoutToken() {
        Message message = new Message(title, body, getAppContext().getString(R.string.message_type_general));
        message.sendMessage(MockDatabase.axel.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void sendNullMessageTest() {
        Message message = new Message(null, null, getAppContext().getString(R.string.message_type_general));
        message.sendMessage(user.getUid());
        device.openNotification();
        assertNull(device.findObject(By.text(title)));
    }

    @Test
    public void extractMessageTest() {
        Map<String, String> message = new HashMap<>();
        message.put(getAppContext().getString(R.string.data_key_title), title);
        message.put(getAppContext().getString(R.string.data_key_body), body);
        message.put(getAppContext().getString(R.string.data_key_type), getAppContext().getString(R.string.message_type_general));
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
