package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.util.http.HttpClient;
import ch.epfl.balelecbud.util.http.MockHttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class MessageTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final HttpClient mockHttpClient = MockHttpClient.getInstance();
    private final User user = MockDatabaseWrapper.celine;
    private final String token = MockDatabaseWrapper.token;

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

    @Test
    public void sendMessageTest() {
        Message message = new Message(Message.MESSAGE_TYPE_GENERAL, "Title", "Body");
        message.sendMessage(user.getUid());
    }

    @Test
    public void setTokenToDatabaseTest() {
        Message.setToken(token);
        Message.setTokenToDatabase();

        TestAsyncUtils sync = new TestAsyncUtils();
        mockDB.getDocument(DatabaseWrapper.TOKENS_PATH, user.getUid())
                .whenComplete((t, throwable) -> {
            if (throwable == null) {
                assertThat(t.get("token"), is(token));
            } else {
                sync.fail(throwable);
            }
            sync.call();
        });
        sync.assertNoFailedTests();
    }

    @Test
    public void cantSetNullTokenToDatabaseTest() {
        Message.setTokenToDatabase();

        TestAsyncUtils sync = new TestAsyncUtils();
        mockDB.getDocument(DatabaseWrapper.TOKENS_PATH, user.getUid())
                .whenComplete((t, throwable) -> {
                    if (throwable == null) {
                        assertNull(t);
                    } else {
                        sync.fail(throwable);
                    }
                    sync.call();
                });
        sync.assertNoFailedTests();
    }

    @Test
    public void cantSetNullToken() {
        Message.setToken(token);
        assertThat(Message.getToken(), is(token));
        Message.setToken(null);
        assertThat(Message.getToken(), is(token));
    }
}
