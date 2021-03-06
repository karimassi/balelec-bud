package ch.epfl.balelecbud.utility.cloudMessaging;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
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
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.notifications.NotificationMessageTest;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

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
                    TokenUtils.setToken(token);
                }
            };

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        NotificationMessageTest.clearNotifications(device);
        mockMessagingService.setContext(mActivityRule.getActivity());
    }

    @Test
    public void sendFriendshipMessageNullType() {
        Message.sendFriendshipMessage(friend, user.getUid(), null);
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.accept_request_title))));
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.friend_request_title))));
    }

    @Test
    public void sendFriendRequestMessageTest() {
        sendFriendshipMessageTest(Message.Type.FRIEND_REQUEST,
                getAppContext().getString(R.string.friend_request_title),
                getAppContext().getString(R.string.friend_request_body));
    }

    @Test
    public void sendFriendRequestMessageWithoutToken() {
        Message.sendFriendshipMessage(MockDatabase.axel, user.getUid(),
                Message.Type.FRIEND_REQUEST);
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.friend_request_title))));
    }

    @Test
    public void sendAcceptRequestMessageTest() {
        sendFriendshipMessageTest(Message.Type.ACCEPT_REQUEST,
                getAppContext().getString(R.string.accept_request_title),
                getAppContext().getString(R.string.accept_request_body));
    }

    @Test
    public void sendAcceptRequestMessageWithoutToken() {
        Message.sendFriendshipMessage(MockDatabase.axel, user.getUid(),
                Message.Type.ACCEPT_REQUEST);
        assertNull(device.findObject(By.text(getAppContext().getString(R.string.accept_request_title))));
    }

    @Test
    public void sendMessageToUserWithToken() {
        Message message = new Message(title, body, getAppContext().getString(R.string.message_type_general));
        message.sendMessage(user.getUid());
        NotificationMessageTest.verifyNotification(device, title);
    }

    @Test
    public void sendMessageToUserWithoutToken() {
        Message message = new Message(title, body, getAppContext().getString(R.string.message_type_general));
        message.sendMessage(MockDatabase.axel.getUid());
        assertFalse(device.wait(Until.hasObject(By.textStartsWith(title)), 5_000));
    }

    @Test
    public void sendNullMessageTest() {
        Message message = new Message(null, null, getAppContext().getString(R.string.message_type_general));
        message.sendMessage(user.getUid());
        assertFalse(device.wait(Until.hasObject(By.textStartsWith(title)), 5_000));
    }

    @Test
    public void extractMessageTest() {
        Map<String, String> message = new HashMap<>();
        message.put(getAppContext().getString(R.string.data_key_title), title);
        message.put(getAppContext().getString(R.string.data_key_body), body);
        message.put(getAppContext().getString(R.string.data_key_type), getAppContext().getString(R.string.message_type_general));
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message).build();
        Map<String, String> result = Message.extractFromMessage(rm);
        assertThat(result, is(message));
    }

    @Test
    public void extractEmptyMessageTest() {
        Map<String, String> message = new HashMap<>();
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message).build();
        Map<String, String> result = Message.extractFromMessage(rm);
        assertTrue(result.isEmpty());
    }

    private void sendFriendshipMessageTest(Message.Type type, String title, String body) {
        Message.sendFriendshipMessage(friend, user.getUid(), type);
        NotificationMessageTest.verifyNotification(device, title);
    }
}
