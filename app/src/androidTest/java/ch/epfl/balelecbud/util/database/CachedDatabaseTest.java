package ch.epfl.balelecbud.util.database;


import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.cache.Cache;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.util.database.Database.FESTIVAL_INFORMATION_PATH;
import static ch.epfl.balelecbud.util.database.Database.LOCATIONS_PATH;
import static ch.epfl.balelecbud.util.database.Database.Source.CACHE;
import static ch.epfl.balelecbud.util.database.Database.Source.REMOTE;

@RunWith(AndroidJUnit4.class)
public class CachedDatabaseTest {

    private Cache appCache;
    private MockDatabase mockDatabase;

    FestivalInformation freshInfo = new FestivalInformation("Information", "Fresh information");
    FestivalInformation staleInfo = new FestivalInformation("Information", "Stale information");

    @Before
    public void setup() throws IOException {
        mockDatabase = MockDatabase.getInstance();
        mockDatabase.resetDatabase();
        appCache = BalelecbudApplication.getAppCache();
        appCache.flush();
        BalelecbudApplication.setRemoteDatabase(mockDatabase);
        mockDatabase.storeDocumentWithID(FESTIVAL_INFORMATION_PATH, "information", freshInfo);
        appCache.put(FESTIVAL_INFORMATION_PATH, "information", staleInfo);
    }

    @Test
    public void getDocumentForceRemote() throws Throwable {
        MyQuery query = new MyQuery(FESTIVAL_INFORMATION_PATH, Collections.emptyList(), REMOTE);
        assertResults(Collections.singletonList(freshInfo),
                getAppDatabase().query(query, FestivalInformation.class));
    }

//    @Test
//    public void getDocumentForceCache() throws Throwable {
//        MyQuery query = new MyQuery(FESTIVAL_INFORMATION_PATH, Collections.emptyList(), CACHE);
//        assertResults(Collections.singletonList(staleInfo),
//                getAppDatabase().query(query, FestivalInformation.class));
//    }

    @Test
    public void allOperationsExceptQueryGoToRemote() throws Throwable{
        Log.d("HERE", getAppDatabase().getClass().getSimpleName());
        getAppDatabase().listenDocument(LOCATIONS_PATH, "0", location -> {}, Location.class);
        Assert.assertEquals(1, MockDatabase.getInstance().getFriendsLocationListenerCount());
        getAppDatabase().unregisterDocumentListener(LOCATIONS_PATH, "0");
        Assert.assertEquals(0, MockDatabase.getInstance().getFriendsLocationListenerCount());

        MyQuery query = new MyQuery(LOCATIONS_PATH, Collections.emptyList());

        getAppDatabase().storeDocumentWithID(LOCATIONS_PATH, "defaultLocation" ,Location.DEFAULT_LOCATION);
        assertResults(Collections.singletonList(Location.DEFAULT_LOCATION),
                mockDatabase.query(query, Location.class));

        getAppDatabase().deleteDocumentWithID(LOCATIONS_PATH, "defaultLocation");
        assertResults(Collections.emptyList(),
                mockDatabase.query(query, Location.class));

        getAppDatabase().storeDocument(LOCATIONS_PATH, Location.DEFAULT_LOCATION);
        assertResults(Collections.singletonList(Location.DEFAULT_LOCATION),
                mockDatabase.query(query, Location.class));

    }

    private <T> void assertResults(List<T> expected, CompletableFuture<List<T>> actual) throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        actual.whenComplete((list, throwable) -> {
            if (throwable == null) {
                sync.assertEquals(expected, list);
                sync.call();
            } else {
                sync.fail();
            }
        });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

}
