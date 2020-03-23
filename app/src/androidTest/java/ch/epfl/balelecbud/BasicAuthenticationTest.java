package ch.epfl.balelecbud;

import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class BasicAuthenticationTest {

    protected void logout() throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MockAuthenticator.getInstance().signOut();
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }

}