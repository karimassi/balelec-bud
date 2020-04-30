package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;

import static ch.epfl.balelecbud.util.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TokenUtilTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final User user = MockDatabase.celine;
    private final String token = MockDatabase.token1;


    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppDatabase(mockDB);
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    mockAuth.signOut();
                    mockAuth.setCurrentUser(user);
                    TokenUtil.setToken(token);
                }
            };

    @Test
    public void storeTokenTest() throws InterruptedException {
        TokenUtil.setToken(token);
        TokenUtil.storeToken();

        TestAsyncUtils sync = new TestAsyncUtils();

        MyQuery query = new MyQuery(Database.TOKENS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, user.getUid()));
        mockDB.query(query)
                .whenComplete((t, throwable) -> {
                    if (throwable == null) {
                        String returned = new ArrayList<>(t.get(0).keySet()).get(0);
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
        TokenUtil.storeToken();

        TestAsyncUtils sync = new TestAsyncUtils();


        MyQuery query = new MyQuery(Database.TOKENS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, user.getUid()));
        mockDB.query(query)
                .whenComplete((t, throwable) -> {
                    if (throwable == null) {
                        String returned = new ArrayList<>(t.get(0).keySet()).get(0);
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
        TokenUtil.setToken(token);
        assertThat(TokenUtil.getToken(), is(token));
        TokenUtil.setToken(null);
        assertThat(TokenUtil.getToken(), is(token));
    }

    @Test
    public void defaultConstructor() {
        new TokenUtil();
    }
}