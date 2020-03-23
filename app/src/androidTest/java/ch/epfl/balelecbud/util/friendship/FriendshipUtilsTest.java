package ch.epfl.balelecbud.util.friendship;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.common.collect.ImmutableMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.LoginUserActivity;
import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.FriendRequest;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
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
//        db = FirestoreDatabaseWrapper.getInstance();
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
                FriendshipUtils.addFriend(friend, new Callback() {
                    @Override
                    public void onSuccess(Object data) {
                    }
                    @Override
                    public void onFailure(String message) {
                        Log.d("FriendshipUtils", message);
                    }
                });
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }
    private void acceptRequest(final FriendRequest request) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FriendshipUtils.acceptRequest(request, new Callback() {
                    @Override
                    public void onSuccess(Object data) {
                    }
                    @Override
                    public void onFailure(String message) {
                        Log.d("FriendshipUtils", message);
                    }
                });
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
                FriendshipUtils.removeFriend(user, new Callback() {
                    @Override
                    public void onSuccess(Object data) {
                    }
                    @Override
                    public void onFailure(String message) {
                        Log.d("FriendshipUtils", message);
                    }
                });
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

        db.getDocument("friendRequests", recipient.getUid(), FriendRequest.class, new Callback<FriendRequest>() {
            @Override
            public void onSuccess(FriendRequest data) {
                Assert.assertEquals(sender.getUid(), data.getSenderId());
                Assert.assertEquals(recipient.getUid(), data.getRecipientId());
            }
            @Override
            public void onFailure(String message) {
                Assert.fail(message);
            }
        });
    }

    @Test
    public void acceptRequestCreatedFrienship() throws Throwable {
        final FriendRequest request = new FriendRequest(sender.getUid(), recipient.getUid());
        final Map<String,Boolean> result= new HashMap<>();
        result.put(request.getSenderId(), true);

        acceptRequest(request);
        db.getDocument("friendships", request.getRecipientId(), HashMap.class, new Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap data) {
                Assert.assertEquals(result, (HashMap<String, Boolean>) data);
            }
            @Override
            public void onFailure(String message) {
                Assert.fail();
            }
        });

    }

    @Test
    public void removeFriendsDeleteFriendshipDB() throws Throwable {
        addFriend(recipient);
        final FriendRequest request = new FriendRequest(sender.getUid(), recipient.getUid());
        acceptRequest(request);
        deleteFriend(recipient);

        db.getDocument("friendships", request.getRecipientId(), HashMap.class, new Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap data) {
                Assert.assertEquals(new HashMap<String, Boolean>(), data);
            }
            @Override
            public void onFailure(String message) {
                Assert.fail();
            }
        });
    }






}
