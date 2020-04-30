package ch.epfl.balelecbud.settings;

import android.app.PendingIntent;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.LocationRequest;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.alex;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;

public class SettingsSignInTest {
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<SettingsActivity>(SettingsActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            BalelecbudApplication.setAppAuthenticator(mockAuth);
            mockAuth.setCurrentUser(alex);
        }
    };

    @Test
    public void whenSignedInSignInIsNotDisplayed() {
        onView(withText(R.string.click_to_sign_in)).check(matches(not(isDisplayed())));
    }

    @Test
    public void whenSignedInSignOutIsDisplayed() {
        onView(withText(R.string.sign_out_text)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutDisableLocation() {
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
            }
        });
        LocationUtil.enableLocation();
        onView(withText(R.string.sign_out_text)).perform(click());
        assertFalse(LocationUtil.isLocationActive());
    }
}
