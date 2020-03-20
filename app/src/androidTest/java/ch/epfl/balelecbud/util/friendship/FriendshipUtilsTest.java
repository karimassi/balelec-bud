package ch.epfl.balelecbud.util.friendship;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.FriendRequest;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

@RunWith(AndroidJUnit4.class)
public class FriendshipUtilsTest {

    Authenticator authenticator;
    DatabaseWrapper db;
    User u;
    User x;

    @Before
    public void setup() {
        authenticator = MockAuthenticator.getInstance();
        db = MockDatabaseWrapper.getInstance();
        FriendshipUtils.setDatabaseImplementation(db);
        FriendshipUtils.setAuthenticator(authenticator);
        u = new User("karim@epfl.ch", null, "karim@epfl.ch", "0");
        x = new User("celine@epfl.ch", null, "celine@epfl.ch", "1");
        authenticator.signOut();
        authenticator.setCurrentUser(u);
    }

    @Test
    public void addFriendCreatesRequest() {
        FriendshipUtils.addFriend(x, new Callback<FriendRequest>() {
            @Override
            public void onSuccess(FriendRequest data) {
                Assert.assertEquals("0", data.getSenderId());
                Assert.assertEquals("1", data.getRecipientId());
            }
            @Override
            public void onFailure(String message) {}
        });
        db.getDocument("friendRequests", "0", FriendRequest.class, new Callback<FriendRequest>() {
            @Override
            public void onSuccess(FriendRequest data) {
                Assert.assertEquals("0", data.getSenderId());
                Assert.assertEquals("1", data.getRecipientId());
            }
            @Override
            public void onFailure(String message) {}
        });
    }

    @Test
    public void acceptRequestCreatedFrienship() {
        FriendRequest request = new FriendRequest("0", "1", 0);
        FriendshipUtils.acceptRequest(request, new Callback<String>() {
            @Override
            public void onSuccess(String data) {}

            @Override
            public void onFailure(String message) {}
        });
        db.getDocument("friendships", "0", FriendRequest.class, new Callback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> data) {
                Assert.assertEquals(new ArrayList<>(Arrays.asList("1")), data);
            }
            @Override
            public void onFailure(String message) {}
        });

    }






}
