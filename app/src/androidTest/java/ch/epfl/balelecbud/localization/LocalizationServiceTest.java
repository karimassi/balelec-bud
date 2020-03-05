package ch.epfl.balelecbud.localization;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.MainActivity;
import ch.epfl.balelecbud.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LocalizationServiceTest {
    FusedLocationProviderClient client;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
               GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.ACCESS_BACKGROUND_LOCATION);

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setupClient() {
        this.client = LocationServices.getFusedLocationProviderClient(mActivityRule.getActivity());
        this.client.setMockMode(true);
    }

    @Test
    public void testCanSwitchOnLocalization() {
        onView(withId(R.id.localizationSwitch)).perform(click());
    }

    @Test
    public void testCanSwitchOffLocalization() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        onView(withId(R.id.localizationSwitch)).perform(click());
    }

}