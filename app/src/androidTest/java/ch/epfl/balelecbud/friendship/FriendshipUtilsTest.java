package ch.epfl.balelecbud.friendship;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.LoginUserActivity;
import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;

@RunWith(AndroidJUnit4.class)
public class FriendshipUtilsTest {

    private final MockDatabase db = MockDatabase.getInstance();
    private final Authenticator authenticator = MockAuthenticator.getInstance();
    private final User sender = MockDatabase.karim;
    private final User recipient = MockDatabase.celine;

    @Before
    public void setup() {
        BalelecbudApplication.setAppDatabase(db);
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

    private void checkResult(CompletableFuture<List<String>> document, List<String> expected)
            throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        document.whenComplete((strings, throwable) -> {
            if (throwable == null) {
                sync.assertEquals(expected, strings);
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
        final List<String> result = new ArrayList<>();
        result.add(sender.getUid());

        MyQuery query = new MyQuery(Database.FRIEND_REQUESTS_PATH,
                new MyWhereClause(Database.DOCUMENT_ID_OPERAND, MyWhereClause.Operator.EQUAL, recipient.getUid()));
        checkResult(db.query(query).thenApply(maps -> new ArrayList<>(maps.get(0).keySet())),result);
    }

    @Test
    public void acceptRequestCreatedFriendship() throws Throwable {
        addFriend(recipient);
        authenticator.signOut();
        authenticator.setCurrentUser(recipient);
        final List<String> result = new ArrayList<>();
        result.add(sender.getUid());

        acceptRequest(sender);

        MyQuery query = new MyQuery(Database.FRIENDSHIPS_PATH,
                new MyWhereClause(Database.DOCUMENT_ID_OPERAND, MyWhereClause.Operator.EQUAL, recipient.getUid()));
        checkResult(db.query(query).thenApply(maps -> new ArrayList<>(maps.get(0).keySet())),result);
    }

    @Test
    public void removeFriendsDeleteFriendshipDB() throws Throwable {
        addFriend(recipient);
        acceptRequest(sender);
        deleteFriend(recipient);

        MyQuery query = new MyQuery(Database.FRIENDSHIPS_PATH,
                new MyWhereClause(Database.DOCUMENT_ID_OPERAND, MyWhereClause.Operator.EQUAL, recipient.getUid()));
        checkResult(db.query(query).thenApply(maps -> new ArrayList<>(maps.get(0).keySet())), new ArrayList<>());
    }
}
