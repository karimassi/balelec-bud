package ch.epfl.balelecbud.friendship;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.LoginUserActivity;
import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;

@RunWith(AndroidJUnit4.class)
public class FriendshipUtilsTest {

    private final DatabaseWrapper db = MockDatabaseWrapper.getInstance();
    private final Authenticator authenticator = MockAuthenticator.getInstance();
    private final User sender = MockDatabaseWrapper.karim;
    private final User recipient = MockDatabaseWrapper.celine;

    @Before
    public void setup() {
        BalelecbudApplication.setAppDatabaseWrapper(db);
        BalelecbudApplication.setAppAuthenticator(authenticator);
        authenticator.signOut();
        authenticator.setCurrentUser(sender);
    }

    @Rule
    public final ActivityTestRule<LoginUserActivity> mActivityRule =
            new ActivityTestRule<>(LoginUserActivity.class);

    private void addFriend(final User friend) throws Throwable {
        runOnUIThreadAndWait(() -> FriendshipUtils.addFriend(friend));
    }

    private void acceptRequest(final User sender) throws Throwable {
        runOnUIThreadAndWait(() -> FriendshipUtils.acceptRequest(sender));
    }

    private void deleteFriend(final User user) throws Throwable {
        runOnUIThreadAndWait(() -> FriendshipUtils.removeFriend(user));
    }

    private void checkResult(CompletableFuture<HashMap> document, Map<String, Boolean> expected)
            throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        document.whenComplete((hashMap, throwable) -> {
            if (throwable == null) {
                sync.assertEquals(expected, hashMap);
            } else {
                sync.fail(throwable);
            }
            sync.call();
        });
        sync.waitCall(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void createFriendshipUtilsTest() {
        new FriendshipUtils();
    }

    @Test
    public void addFriendCreatesRequest() throws Throwable{
        addFriend(recipient);

        final Map<String, Boolean> result = new HashMap<>();
        result.put(sender.getUid(), true);

        checkResult(db.getCustomDocument("friendRequests", recipient.getUid(), HashMap.class),
                result);
    }

    @Test
    public void acceptRequestCreatedFriendship() throws Throwable {
        authenticator.signOut();
        authenticator.setCurrentUser(recipient);
        final Map<String, Boolean> result = new HashMap<>();
        result.put(sender.getUid(), true);

        acceptRequest(sender);

        checkResult(db.getCustomDocument("friendships", recipient.getUid(), HashMap.class),
                result);
    }

    @Test
    public void removeFriendsDeleteFriendshipDB() throws Throwable {
        addFriend(recipient);
        acceptRequest(sender);
        deleteFriend(recipient);

        checkResult(db.getCustomDocument("friendships", recipient.getUid(), HashMap.class),
                new HashMap<>());
    }
}
