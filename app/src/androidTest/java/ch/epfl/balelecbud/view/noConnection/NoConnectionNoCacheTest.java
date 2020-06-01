package ch.epfl.balelecbud.view.noConnection;

import android.os.SystemClock;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.cache.Cache;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class NoConnectionNoCacheTest extends NoConnectionSuit {
    private final Cache emptyCache = new Cache() {
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

    @Override
    protected Cache getCache() {
        return emptyCache;
    }

    @Test
    public void noConnectionIsDisplayedWhenOpenSchedule() {
        openDrawer();
        clickItem(R.id.fragment_main_drawer_schedule);
        SystemClock.sleep(100);
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
