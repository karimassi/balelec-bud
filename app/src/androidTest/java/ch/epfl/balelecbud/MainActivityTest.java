package ch.epfl.balelecbud;


import android.Manifest;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.Authentication.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Rule
    public final GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION
    );

    @Before
    public void setUp() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                activity.setAuthenticator(MockAuthenticator.getInstance());
            }
        });
        MockAuthenticator.getInstance().signOut();
    }

//    @Test
//    public void testLoggedInGoesToWelcomeActivity() {
//        onView(withId(R.id.editTextEmailLogin)).perform(typeText("karim@epfl.ch")).perform(closeSoftKeyboard());
//        onView(withId(R.id.editTextPasswordLogin)).perform(typeText("123456")).perform(closeSoftKeyboard());
//        onView(withId(R.id.buttonLogin)).perform(click());
//        onView(withId(R.id.buttonSignOut)).check(matches(isDisplayed()));
//
//    }

    @Test
    public void testLoggedOutGoesToLoginActivity() {
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

}
