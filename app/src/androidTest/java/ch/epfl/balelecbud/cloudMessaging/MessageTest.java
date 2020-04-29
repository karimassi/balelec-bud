package ch.epfl.balelecbud.cloudMessaging;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
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

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MessageTest {

    private final String TAG = MessageTest.class.getSimpleName();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final MessagingService mockMessagingService = getMessagingService();
    private final User user = MockDatabaseWrapper.celine;
    private final String token = MockDatabaseWrapper.token;
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
        clearNotifications();
        /*
        mockMessagingService.setContext(mActivityRule.getActivity());

         */
    }

    @After
    public void tearDown() {
        clearNotifications();
    }

    @Test
    public void sendMessageToUserWithToken() {
        Message message = new Message(title, body, Message.MESSAGE_TYPE_GENERAL);
        message.sendMessage(user.getUid());
        verifyNotification();
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
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message)
                .setMessageType(Message.MESSAGE_TYPE_GENERAL).build();
        message.put(Message.DATA_KEY_TYPE, Message.MESSAGE_TYPE_GENERAL);
        Map<String, String> result = Message.extractMessage(rm);
        assertThat(result, is(message));
    }

    @Test
    public void extractEmptyMessageTest() {
        Map<String, String> message = new HashMap<>();
        RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message)
                .setMessageType(Message.MESSAGE_TYPE_GENERAL).build();
        Map<String, String> result = Message.extractMessage(rm);
        assertTrue(result.isEmpty());
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

    private MessagingService getMessagingService() {
        return new MessagingService() {
            @Override
            public void sendMessage(JSONObject send) {
                Map<String, String> message = new HashMap<>();
                try {
                    JSONObject data = (JSONObject) send.get("data");
                    message.put(Message.DATA_KEY_TITLE, (String) data.get(Message.DATA_KEY_TITLE));
                    message.put(Message.DATA_KEY_BODY, (String) data.get(Message.DATA_KEY_BODY));
                    RemoteMessage rm = new RemoteMessage.Builder("ID").setData(message)
                            .setMessageType((String) data.get(Message.DATA_KEY_TYPE)).build();
                    this.receiveMessage(rm);
                } catch (JSONException e) {
                    Log.d(TAG, "Couldn't get information from test JSONObject");
                }
            }

            @Override
            public void receiveMessage(RemoteMessage remoteMessage) {
                Map<String, String> message = Message.extractMessage(remoteMessage);
                if(message.isEmpty()) {
                    return;
                }
                Log.d(TAG, "About to send test notification");
                NotificationMessage.getInstance().scheduleNotification(mActivityRule.getActivity(), message);
            }
        };
    }
}
