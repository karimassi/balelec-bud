package ch.epfl.balelecbud;

import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public abstract class BasicAuthenticationTest {

    protected void logout() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        Runnable myRunnable = () -> {
            MockAuthenticator.getInstance().signOut();
            sync.call();
        };
        runOnUiThread(myRunnable);
        sync.waitCall(1);
    }
}