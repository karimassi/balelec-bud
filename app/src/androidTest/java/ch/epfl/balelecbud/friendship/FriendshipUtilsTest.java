package ch.epfl.balelecbud.friendship;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.LoginUserActivity;
import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

@RunWith(AndroidJUnit4.class)
public class FriendshipUtilsTest {

    private final DatabaseWrapper db = MockDatabaseWrapper.getInstance();
    private Authenticator authenticator = MockAuthenticator.getInstance();
    private User sender;
    private User recipient;

    @Before
    public void setup() {
        BalelecbudApplication.setAppDatabaseWrapper(db);
        BalelecbudApplication.setAppAuthenticator(authenticator);
        sender = MockDatabaseWrapper.karim;
        recipient = MockDatabaseWrapper.celine;
        authenticator.signOut();
        authenticator.setCurrentUser(sender);
    }

    @Rule
    public final ActivityTestRule<LoginUserActivity> mActivityRule =
            new ActivityTestRule<>(LoginUserActivity.class);

    private void addFriend(final User friend) throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        Runnable myRunnable = () -> {
            FriendshipUtils.addFriend(friend);
            sync.call();
        };
        runOnUiThread(myRunnable);
        sync.waitCall(1);
    }

    private void acceptRequest(final User sender) throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        Runnable myRunnable = () -> {
            FriendshipUtils.acceptRequest(sender);
            sync.call();
        };
        runOnUiThread(myRunnable);
        sync.waitCall(1);
    }

    private void deleteFriend(final User user) throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        Runnable myRunnable = () -> {
            FriendshipUtils.removeFriend(user);
            sync.call();
        };
        runOnUiThread(myRunnable);
        sync.waitCall(1);
    }

    @Test
    public void createFriendshipUtilsTest() {
        new FriendshipUtils();
    }

    @Test
    public void addFriendCreatesRequest() throws Throwable{
        addFriend(recipient);

        final Map<String,Boolean> result = new HashMap<>();
        result.put(sender.getUid(), true);

        TestAsyncUtils sync = new TestAsyncUtils();

        db.getCustomDocument("friendRequests", recipient.getUid(), HashMap.class)
                .whenComplete((hashMap, throwable) -> {
                    if (throwable == null) {
                        sync.assertEquals(result, hashMap);
                    } else {
                        sync.fail(throwable);
                    }
                    sync.call();
                });
        sync.waitCall(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void acceptRequestCreatedFriendship() throws Throwable {
        authenticator.signOut();
        authenticator.setCurrentUser(recipient);
        final Map<String,Boolean> result = new HashMap<>();
        result.put(sender.getUid(), true);

        acceptRequest(sender);

        TestAsyncUtils sync = new TestAsyncUtils();

        db.getCustomDocument("friendships", recipient.getUid(), HashMap.class)
                .whenComplete((hashMap, throwable) -> {
                    if (throwable == null) {
                        sync.assertEquals(result, hashMap);
                    } else {
                        sync.fail(throwable);
                    }
                    sync.call();
                });
        sync.waitCall(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void removeFriendsDeleteFriendshipDB() throws Throwable {
        addFriend(recipient);
        acceptRequest(sender);
        deleteFriend(recipient);

        TestAsyncUtils sync = new TestAsyncUtils();

        db.getCustomDocument("friendships", recipient.getUid(), HashMap.class)
                .whenComplete((hashMap, throwable) -> {
                    if (throwable == null) {
                        sync.assertEquals(new HashMap<String, Boolean>(), hashMap);
                    } else {
                        sync.fail(throwable);
                    }
                    sync.call();
                });
        sync.waitCall(1);
        sync.assertNoFailedTests();
    }
}
