
package ch.epfl.balelecbud;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() throws Throwable {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                activity.setAuthenticator(MockAuthenticator.getInstance());
            }
        });
        Runnable myRunnable = new Runnable(){
            @Override
            public void run() {
                MockAuthenticator.getInstance().signOut();
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }
    }

    @Test
    public void testLoggedOutGoesToLoginActivity() {
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }
}