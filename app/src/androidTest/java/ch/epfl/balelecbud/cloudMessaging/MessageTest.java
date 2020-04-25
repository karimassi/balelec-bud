package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MessageTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final User user = MockDatabaseWrapper.celine;
    private final String token = "TheBestTestToken";
    private final Map<String, String> toStore = new HashMap<>();

    @Before
    public void setup() {
        BalelecbudApplication.setAppDatabaseWrapper(mockDB);
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        mockAuth.signOut();
        mockAuth.setCurrentUser(user);
        toStore.put("token", token);
        mockDB.storeDocumentWithID(DatabaseWrapper.TOKENS_PATH, user.getUid(), toStore);
    }

    @Test
    public void sendMessageTest() {
        Message message = new Message(Message.MESSAGE_TYPE_GENERAL, "Title", "Body");

        message.sendMessage(user.getUid());


    }

    @Test
    public void setTokenToDatabaseTest() {
        Message.setToken(token);
        Message.setTokenToDatabase();
        User user = mockAuth.getCurrentUser();

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
}
