package ch.epfl.balelecbud.view.noConnection;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Rule;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.cache.Cache;
import ch.epfl.balelecbud.utility.cache.FilesystemCache;
import ch.epfl.balelecbud.utility.connectivity.AndroidConnectivityChecker;
import ch.epfl.balelecbud.utility.database.CachedDatabase;
import ch.epfl.balelecbud.utility.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.utility.database.MockDatabase.alex;

abstract class NoConnectionSuit {
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RootActivity> testRule = new ActivityTestRule<RootActivity>(RootActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            BalelecbudApplication.setConnectivityChecker(() -> false);
            MockAuthenticator mockAuth = MockAuthenticator.getInstance();
            BalelecbudApplication.setAppAuthenticator(mockAuth);
            mockAuth.setCurrentUser(alex);
            BalelecbudApplication.setAppCache(getCache());
            CachedDatabase.getInstance().setCache(getCache());
            BalelecbudApplication.setRemoteDatabase(MockDatabase.getInstance());
        }
    };

    protected abstract Cache getCache();

    @After
    public void cleanUp() {
        BalelecbudApplication.setAppCache(FilesystemCache.getInstance());
        CachedDatabase.getInstance().setCache(FilesystemCache.getInstance());
        BalelecbudApplication.setConnectivityChecker(AndroidConnectivityChecker.getInstance());
    }

    void openDrawer() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
    }

    void clickItem(int itemId) {
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(itemId));
        device.waitForIdle();
    }
}
