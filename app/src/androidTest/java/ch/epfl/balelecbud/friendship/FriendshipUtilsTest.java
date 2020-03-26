package ch.epfl.balelecbud.friendship;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import ch.epfl.balelecbud.LoginUserActivity;
import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.FriendRequest;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

@RunWith(AndroidJUnit4.class)
public class FriendshipUtilsTest {

    Authenticator authenticator;
    DatabaseWrapper db;
    User sender;
    User recipient;

    @Before
    public void setup() {
        authenticator = MockAuthenticator.getInstance();
        db = MockDatabaseWrapper.getInstance();
        FriendshipUtils.setDatabaseImplementation(db);
        FriendshipUtils.setAuthenticator(authenticator);
//        authenticator.createAccount("karim@epfl.ch", "1234");
//        authenticator.createAccount("celine@epfl.ch", "1234");
        sender = new User("karim@epfl.ch", "karim@epfl.ch", "0");
        recipient = new User("celine@epfl.ch", "celine@epfl.ch", "1");
        authenticator.signOut();
        authenticator.setCurrentUser(sender);
    }

    @Rule
    public final ActivityTestRule<LoginUserActivity> mActivityRule = new ActivityTestRule<LoginUserActivity>(LoginUserActivity.class);

    private void addFriend(final User friend) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FriendshipUtils.addFriend(friend);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }
    private void acceptRequest(final User sender) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FriendshipUtils.acceptRequest(sender);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }
    private void deleteFriend(final User user) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FriendshipUtils.removeFriend(user);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }

    @Test
    public void createFriendshipUtilsTest() {
        new FriendshipUtils();
    }

    @Test
    public void addFriendCreatesRequest() throws Throwable{

        addFriend(recipient);

        final Map<String,Boolean> result= new HashMap<>();
        result.put(sender.getUid(), true);

        db.getCustomDocument("friendRequests", recipient.getUid(), HashMap.class).whenComplete(new BiConsumer<HashMap, Throwable>() {
            @Override
            public void accept(HashMap hashMap, Throwable throwable) {
                if (throwable != null) {
                    Assert.assertEquals(result, hashMap);
                } else {
                    Assert.fail();
                }
            }
        });
    }

    @Test
    public void acceptRequestCreatedFrienship() throws Throwable {
        final Map<String,Boolean> result= new HashMap<>();
        result.put(sender.getUid(), true);

        acceptRequest(sender);
        db.getCustomDocument("friendships", recipient.getUid(), HashMap.class).whenComplete(new BiConsumer<HashMap, Throwable>() {
            @Override
            public void accept(HashMap hashMap, Throwable throwable) {
                if (throwable!=null) {
                    Assert.assertEquals(result, (HashMap<String, Boolean>) hashMap);
                } else {
                    Assert.fail();
                }
            }
        });

    }

    @Test
    public void removeFriendsDeleteFriendshipDB() throws Throwable {
        addFriend(recipient);
        acceptRequest(sender);
        deleteFriend(recipient);

        db.getCustomDocument("friendships", recipient.getUid(), HashMap.class).whenComplete(new BiConsumer<HashMap, Throwable>() {
            @Override
            public void accept(HashMap hashMap, Throwable throwable) {
                if (throwable != null) {
                    Assert.assertEquals(new HashMap<String, Boolean>(), hashMap);
                } else {
                    Assert.fail();
                }
            }
        });
    }






}
