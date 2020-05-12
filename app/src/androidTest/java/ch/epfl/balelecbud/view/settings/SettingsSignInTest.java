package ch.epfl.balelecbud.view.settings;

import android.app.PendingIntent;

import androidx.fragment.app.testing.FragmentScenario;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.location.LocationClient;
import ch.epfl.balelecbud.utility.location.LocationUtils;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppAuthenticator;
import static ch.epfl.balelecbud.utility.database.MockDatabase.alex;
import static org.junit.Assert.assertFalse;

public class SettingsSignInTest {
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    @Before
    public void setUp() {
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        mockAuth.setCurrentUser(alex);
        FragmentScenario.launchInContainer(SettingsFragment.class, null, R.style.Theme_AppCompat, null);
    }

    @Test
    public void whenSignedInSignInIsNotDisplayed() {
        onView(withText(R.string.click_to_sign_in)).check(doesNotExist());
    }

    @Test
    public void whenSignedInSignOutIsDisplayed() {
        onView(withText(R.string.sign_out_text)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutDisableLocation() {
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) { }
            @Override
            public void removeLocationUpdates(PendingIntent intent) { }
        });
        LocationUtils.enableLocation();
        onView(withText(R.string.sign_out_text)).perform(click());
        assertFalse(LocationUtils.isLocationActive());
    }

    @Test
    public void signOutCreatesAnonymousUser() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        setAppAuthenticator(new MockAuthenticator() {
            @Override
            public CompletableFuture<String> signInAnonymously() {
                sync.call();
                return null;
            }

            @Override
            public void signOut() {
                sync.call();
            }
        });
        onView(withText(R.string.sign_out_text)).perform(click());
        sync.waitCall(2);
        sync.assertCalled(2);
    }

    @Test
    public void whenSignedInCanDeleteUser() {
        onView(withText(R.string.delete_account)).check(matches(isDisplayed())).perform(click());
        onView(withText(R.string.delete_account)).check(matches(isDisplayed()));
        onView(withText(R.string.delete_account_text)).check(matches(isDisplayed()));
        onView(withText(R.string.cancel)).check(matches(isDisplayed()));
        onView(withText(R.string.delete_account_yes)).check(matches(isDisplayed()));
    }
}
