package ch.epfl.balelecbud.utility.authentication;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.view.map.MapViewFragment;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.setAppDatabase;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class AnonymousTest {
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private TestAsyncUtils sync;

    @Rule
    public final ActivityTestRule<RootActivity> mActivityRule = new ActivityTestRule<RootActivity>(RootActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            sync = new TestAsyncUtils();
            setAppDatabase(mockDB);
            setAppAuthenticator(new MockAuthenticator() {
                @Override
                public CompletableFuture<String> signInAnonymously() {
                    sync.call();
                    return null;
                }
            });
        }
    };

    private void openFragment(int itemId) {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(itemId));
        device.waitForIdle();
    }

    @Test
    public void whenNotSignedInCreateAnonymousUser() throws InterruptedException {
        device.waitForIdle();
        sync.waitCall(1);
        sync.assertCalled(1);
    }

    @Test
    public void whenNotSignedInSocialIsDisable() {
        openFragment(R.id.activity_main_drawer_social);
        onView(withText(R.string.require_sign_in))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.tabs_social)).check(doesNotExist());
    }

    @Test
    public void whenNotSignedInMapWorks() {
        MapViewFragment.setMockCallback(mapboxMap -> {});
        openFragment(R.id.activity_main_drawer_map);
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }
}
