package ch.epfl.balelecbud.view;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.cache.Cache;
import ch.epfl.balelecbud.utility.cache.FilesystemCache;
import ch.epfl.balelecbud.utility.connectivity.AndroidConnectivityChecker;
import ch.epfl.balelecbud.utility.database.CachedDatabase;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.utility.database.MockDatabase.alex;

@RunWith(AndroidJUnit4.class)
public class NoConnectionNoCacheTest {
    private final Database mockDB = MockDatabase.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());
    private static final Cache emptyCache = new Cache() {
        @Override
        public boolean contains(MyQuery query) { return false; }
        @Override
        public <T> CompletableFuture<FetchedData<T>> get(MyQuery query, Class<T> tClass) {
            throw new NullPointerException();
        }
        @Override
        public CompletableFuture<FetchedData<Map<String, Object>>> get(MyQuery query) {
            throw new NullPointerException();
        }
        @Override
        public void put(String collectionName, String id, Object document) { }
        @Override
        public void flush(String collectionName) { }
        @Override
        public void flush() { }
    };

    @Rule
    public final ActivityTestRule<RootActivity> testRule = new ActivityTestRule<RootActivity>(RootActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            BalelecbudApplication.setConnectivityChecker(() -> false);
            BalelecbudApplication.setAppAuthenticator(mockAuth);
            mockAuth.setCurrentUser(alex);
            BalelecbudApplication.setAppCache(emptyCache);
            CachedDatabase.getInstance().setCache(emptyCache);
            BalelecbudApplication.setRemoteDatabase(mockDB);
        }
    };

    @After
    public void cleanUp() {
        BalelecbudApplication.setAppCache(FilesystemCache.getInstance());
        CachedDatabase.getInstance().setCache(FilesystemCache.getInstance());
        BalelecbudApplication.setConnectivityChecker(AndroidConnectivityChecker.getInstance());
    }

    private void openDrawer() {
        device.pressBack();
        onView(withId(R.id.root_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        device.waitForIdle();
    }

    private void clickItem(int itemId) {
        onView(withId(R.id.root_activity_nav_view)).perform(NavigationViewActions.navigateTo(itemId));
        device.waitForIdle();
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenSchedule() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_schedule);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenInformation() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_info);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenMap() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_map);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenEmergency() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_emergency_info);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenPOI() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_poi);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenSocial() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_social);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenPhotoGallery() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_gallery);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenPlaylist() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_playlist);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenTransport() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_transport);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }
}
