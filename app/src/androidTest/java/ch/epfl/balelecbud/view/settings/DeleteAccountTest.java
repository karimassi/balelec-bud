package ch.epfl.balelecbud.view.settings;

import android.app.PendingIntent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;
import ch.epfl.balelecbud.utility.location.LocationClient;
import ch.epfl.balelecbud.utility.location.LocationUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppDatabase;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.MockDatabase.alex;
import static ch.epfl.balelecbud.utility.database.MockDatabase.axel;
import static ch.epfl.balelecbud.utility.database.MockDatabase.celine;
import static ch.epfl.balelecbud.utility.database.MockDatabase.karim;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DeleteAccountTest {
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDB = MockDatabase.getInstance();

    @Before
    public void setUp() {
        setAppDatabase(mockDB);
        setAppAuthenticator(mockAuth);
        mockDB.resetDatabase();
        mockAuth.signOut();
        mockAuth.setCurrentUser(axel);
        FriendshipUtils.addFriend(alex);
        mockAuth.signOut();
        mockAuth.setCurrentUser(alex);
        mockDB.storeDocumentWithID(Database.LOCATIONS_PATH, alex.getUid(), new Location(12, 42));
        FriendshipUtils.acceptRequest(karim);
        FriendshipUtils.addFriend(celine);
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) { }
            @Override
            public void removeLocationUpdates(PendingIntent intent) { }
        });
        LocationUtils.enableLocation();
        FragmentScenario.launchInContainer(SettingsFragment.class, null, R.style.Theme_AppCompat, null);
    }

    @Test
    public void whenDeleteAccountSignOut() {
        onView(withText(R.string.delete_account)).perform(click());
        onView(withText(R.string.delete_account_yes)).perform(click());
        onView(withText(R.string.not_sign_in)).check(matches(isDisplayed()));
        assertNull(mockAuth.getCurrentUser());
        assertNotEquals(alex.getUid(), mockAuth.getCurrentUid());
        assertFalse(LocationUtils.isLocationActive());
    }

    @Test
    public void whenDeleteAccountDeleteUser() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        setAppAuthenticator(new MockAuthenticator() {
            @Override
            public User getCurrentUser() {
                return alex;
            }

            @Override
            public String getCurrentUid() {
                return alex.getUid();
            }

            @Override
            public CompletableFuture<Void> deleteCurrentUser() {
                sync.call();
                CompletableFuture<Void> future = new CompletableFuture<>();
                future.complete(null);
                return future;
            }
        });
        onView(withText(R.string.delete_account)).perform(click());
        onView(withText(R.string.delete_account_yes)).perform(click());
        sync.waitCall(1);
        sync.assertCalled(1);
    }

    private <T> void deleteUserAndQuery(String collectionName, Class<T> clazz) throws ExecutionException, InterruptedException {
        onView(withText(R.string.delete_account)).perform(click());
        onView(withText(R.string.delete_account_yes)).perform(click());
        MyQuery query = new MyQuery(collectionName, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, alex.getUid()));
        if (clazz != null) {
            assertThat(mockDB.query(query, clazz).get(), is(Collections.singletonList(null)));
        } else {
            assertThat(mockDB.query(query).get(), is(Collections.singletonList(null)));
        }
    }

    @Test
    public void whenDeleteAccountDeleteLocation() throws ExecutionException, InterruptedException {
        deleteUserAndQuery(Database.LOCATIONS_PATH, Location.class);
    }

    @Test
    public void whenDeleteAccountDeleteFriends() throws ExecutionException, InterruptedException {
        deleteUserAndQuery(Database.FRIENDSHIPS_PATH, null);
    }

    @Test
    public void whenDeleteAccountDeleteFriendRequests() throws ExecutionException, InterruptedException {
        deleteUserAndQuery(Database.FRIEND_REQUESTS_PATH, null);
    }

    @Test
    public void whenDeleteAccountDeleteSentFriendRequests() throws ExecutionException, InterruptedException {
        deleteUserAndQuery(Database.SENT_REQUESTS_PATH, null);
    }
}
