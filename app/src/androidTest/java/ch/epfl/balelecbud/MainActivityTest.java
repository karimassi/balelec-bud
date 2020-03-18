
package ch.epfl.balelecbud;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public void grantPermission() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    @Before
    public void setUp() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                activity.setAuthenticator(MockAuthenticator.getInstance());
            }
        });
    }

    @Test
    public void testLoggedOutGoesToLoginActivity() throws Throwable {
        Runnable myRunnable = new Runnable(){
            @Override
            public void run() {
                MockAuthenticator.getInstance().signOut();
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
            ActivityScenario.launch(MainActivity.class);
        }
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }
}