package ch.epfl.balelecbud.cloudMessaging;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.WelcomeActivity;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TokenUtilTest {

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final User user = MockDatabaseWrapper.celine;
    private final String token = MockDatabaseWrapper.token1;

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppDatabaseWrapper(mockDB);
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
        mockDB.getDocument(DatabaseWrapper.TOKENS_PATH, user.getUid())
                .whenComplete((t, throwable) -> {
                    if (throwable == null) {
                        sync.assertThat(t.get("token"), is(token));
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
        mockDB.getDocument(DatabaseWrapper.TOKENS_PATH, user.getUid())
                .whenComplete((t, throwable) -> {
                    if (throwable == null) {
                        sync.assertTrue(t != null);
                        sync.assertThat(t.get("token"), is(token));
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