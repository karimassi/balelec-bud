package ch.epfl.balelecbud.utility.cloudMessaging;

import androidx.test.core.app.ApplicationProvider;
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

public class CloudMessagingServiceTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final CloudMessagingService cloudMessagingService = new CloudMessagingService();
    private final User user = MockDatabase.celine;
    private final String token = MockDatabase.token1;


    private UiDevice device;

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    MockDatabase.getInstance().resetDatabase();
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
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
    }

    @Test
    public void onNewTokenTest() {
        cloudMessagingService.onNewToken(token);
        assertThat(TokenUtils.getToken(), is(token));
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
                .setMessageType(getAppContext().getString(R.string.message_type_general)).build();
    }
}