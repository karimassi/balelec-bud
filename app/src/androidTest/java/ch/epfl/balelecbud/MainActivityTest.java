package ch.epfl.balelecbud;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.Activities.MainActivity;
import ch.epfl.balelecbud.Authentication.FirebaseAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testIsLoggedIn() throws Exception {

        final Object syncObject = new Object();

        FirebaseAuthenticator.getInstance().signIn("karim@epfl.ch", "123456", new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                synchronized (syncObject){
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject){
            syncObject.wait();
        }

        mActivityRule.launchActivity(new Intent());


        onView(withId(R.id.greetingTextView)).check(matches(withText("Welcome !")));

    }

    @Test
    public void testIsNotLoggedIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuthenticator.getInstance().signOut();
        }
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));

    }

    @Test
    public void testSignOut() throws Exception {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            onView(withId(R.id.buttonSignOut)).perform(click());
            onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        } else {

            final Object syncObject = new Object();
            FirebaseAuthenticator.getInstance().signIn("karim@epfl.ch", "123456", new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    synchronized (syncObject){
                        syncObject.notify();
                    }
                }
            });

            synchronized (syncObject) {
                syncObject.wait();
            }

            onView(withId(R.id.buttonSignOut)).perform(click());
            onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));

        }
    }
}
