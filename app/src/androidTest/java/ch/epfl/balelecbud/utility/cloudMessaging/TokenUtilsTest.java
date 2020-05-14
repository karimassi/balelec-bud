package ch.epfl.balelecbud.utility.cloudMessaging;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TokenUtilsTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final User user = MockDatabase.celine;
    private final String token = MockDatabase.token1;


    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    mockDB.resetDatabase();
                    BalelecbudApplication.setAppDatabase(mockDB);
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    mockAuth.signOut();
                    mockAuth.setCurrentUser(user);
                    TokenUtils.setToken(token);
                }
            };

    @Test
    public void storeTokenTest() throws InterruptedException {
        TokenUtils.setToken(token);
        TokenUtils.storeToken();

        TestAsyncUtils sync = new TestAsyncUtils();

        MyQuery query = new MyQuery(Database.TOKENS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, user.getUid()));
        mockDB.query(query)
                .whenComplete((t, throwable) -> {
                    if (throwable == null) {
                        String returned = new ArrayList<>(t.getList().get(0).keySet()).get(0);
                        sync.assertThat(returned, is(token));
                    } else {
                        sync.fail(throwable);
                    }
                    sync.call();
                });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void storeNullTokenTest() throws InterruptedException {
        TokenUtils.storeToken();

        TestAsyncUtils sync = new TestAsyncUtils();


        MyQuery query = new MyQuery(Database.TOKENS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, user.getUid()));
        mockDB.query(query)
                .whenComplete((t, throwable) -> {
                    if (throwable == null) {
                        String returned = new ArrayList<>(t.getList().get(0).keySet()).get(0);
                        sync.assertTrue(returned != null);
                        sync.assertThat(returned, is(token));
                    } else {
                        sync.fail(throwable);
                    }
                    sync.call();
                });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void setNullTokenTest() {
        TokenUtils.setToken(token);
        assertThat(TokenUtils.getToken(), is(token));
        TokenUtils.setToken(null);
        assertThat(TokenUtils.getToken(), is(token));
    }

    @Test
    public void defaultConstructor() {
        new TokenUtils();
    }
}