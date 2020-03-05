package ch.epfl.balelecbud.localization;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.permission.PermissionRequester;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.junit.Assert;
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
    private FusedLocationProviderClient client;

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
        PermissionRequester pr = new PermissionRequester();
        pr.addPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        pr.requestPermissions();
        onView(withId(R.id.localizationSwitch)).perform(click());
        Assert.assertTrue(mActivityRule.getActivity().isLocalizationActive());
    }

    @Test
    public void testCanSwitchOffLocalization() {
        onView(withId(R.id.localizationSwitch)).perform(click());
        onView(withId(R.id.localizationSwitch)).perform(click());
        Assert.assertFalse(mActivityRule.getActivity().isLocalizationActive());
    }

}