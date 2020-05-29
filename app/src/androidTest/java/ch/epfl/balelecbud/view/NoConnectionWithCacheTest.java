package ch.epfl.balelecbud.view;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.cache.Cache;
import ch.epfl.balelecbud.utility.database.CachedDatabase;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.balelecbud.utility.database.MockDatabase.alex;

@RunWith(AndroidJUnit4.class)
public class NoConnectionWithCacheTest {
    private final Database mockDB = MockDatabase.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());
    private static final Cache emptyCache = new Cache() {
        @Override
        public boolean contains(MyQuery query) { return true; }
        @Override
        public <T> CompletableFuture<FetchedData<T>> get(MyQuery query, Class<T> tClass) {
            return CompletableFuture.completedFuture(new FetchedData<>(Collections.emptyList()));
        }
        @Override
        public CompletableFuture<FetchedData<Map<String, Object>>> get(MyQuery query) {
            return CompletableFuture.completedFuture(new FetchedData<>(Collections.emptyList()));
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
            CachedDatabase.getInstance().setCache(emptyCache);
            BalelecbudApplication.setRemoteDatabase(mockDB);
        }
    };

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
    public void noConnectionIsNotDisplayedWhenOpenSchedule() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_schedule);
        onView(withId(R.id.no_connection_image_view)).check(doesNotExist());
    }

    @Test
    public void noConnectionIsNotDisplayedWhenOpenInformation() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_info);
        onView(withId(R.id.no_connection_image_view)).check(doesNotExist());
    }

    @Test
    public void noConnectionIsNotDisplayedWhenOpenMap() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_map);
        onView(withId(R.id.no_connection_image_view)).check(doesNotExist());
    }

    @Test
    public void noConnectionIsNotDisplayedWhenOpenEmergency() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_emergency_info);
        onView(withId(R.id.no_connection_image_view)).check(doesNotExist());
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
    public void noConnectionIsNotDisplayedWhenOpenPlaylist() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_playlist);
        onView(withId(R.id.no_connection_image_view)).check(doesNotExist());
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenTransport() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_transport);
        onView(withId(R.id.no_connection_image_view)).check(matches(isDisplayed()));
    }
}
