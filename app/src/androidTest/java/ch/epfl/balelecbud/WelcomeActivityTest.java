package ch.epfl.balelecbud;


import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<>(WelcomeActivity.class);

    @Rule
    public final GrantPermissionRule fineLocation = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION
    );

//    @RequiresApi(Build.VERSION_CODES.Q)
//    @Rule
//    public final GrantPermissionRule backgroundLocation = GrantPermissionRule.grant(
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION
//    );

    @Test
    public void testCanLogOut() {
        onView(withId(R.id.buttonSignOut)).perform(click());
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

}
