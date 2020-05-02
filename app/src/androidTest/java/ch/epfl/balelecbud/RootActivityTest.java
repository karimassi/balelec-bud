package ch.epfl.balelecbud;

import android.app.PendingIntent;
import android.content.Intent;
import android.view.Gravity;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.map.MapViewFragment;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.http.MockHttpClient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.util.database.MockDatabase.celine;

public abstract class RootActivityTest {
    private static UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule =
            new ActivityTestRule<RootActivity>(RootActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    setUpBeforeActivityLaunched();
                }

                @Override
                protected Intent getActivityIntent() {
                    return addInfoToActivityIntent(
                            new Intent(ApplicationProvider.getApplicationContext(), RootActivity.class));
                }
            };

    protected Intent addInfoToActivityIntent(Intent intent) {
        return intent;
    }

    protected void setUpBeforeActivityLaunched() {
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        MockAuthenticator.getInstance().setCurrentUser(celine);
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
        BalelecbudApplication.setHttpClient(MockHttpClient.getInstance());
        MapViewFragment.setMockCallback(mapboxMap -> {
        });
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
            }
        });
    }

    protected void openFragmentUnderTest() {
        openDrawer();
        clickItem(getItemId(), getViewToDisplayId());
    }

    protected abstract int getItemId();
    protected abstract int getViewToDisplayId();

    @Before
    public void grantPermission() {
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }

        openFragmentUnderTest();
    }

    @Test
    public void testBackPress() {
        openDrawer();
        device.pressBack();
        device.waitForIdle();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        device.pressBack();
        device.waitForIdle();
    }

    public static void openDrawer() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
        onView(withId(R.id.root_activity_nav_view)).check(matches(isDisplayed()));
    }

    public static void clickItem(int itemId, int viewToDisplayId) {
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(itemId));
        device.waitForIdle();
        onView(withId(viewToDisplayId)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutFromDrawer() {
        openDrawer();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    public void waitIdle() {
        device.waitForIdle();
    }
}
