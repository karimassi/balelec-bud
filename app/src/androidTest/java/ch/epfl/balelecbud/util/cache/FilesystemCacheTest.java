package ch.epfl.balelecbud.util.cache;


import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.database.MyGeoClause;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;

import static ch.epfl.balelecbud.util.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.util.database.MockDatabase.axel;
import static ch.epfl.balelecbud.util.database.MockDatabase.karim;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot2;
import static ch.epfl.balelecbud.util.database.MockDatabase.token1;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;

@RunWith(AndroidJUnit4.class)
public class FilesystemCacheTest {

    private Cache appCache = BalelecbudApplication.getAppCache();

    @Before
    public void setup() {
        BalelecbudApplication.setRemoteDatabase(MockDatabase.getInstance());
        appCache.flush();
    }

    @Test
    public void containsCorrectForCollectionRetrieval() throws IOException {
        MyQuery query = new MyQuery(Database.POINT_OF_INTEREST_PATH, Collections.emptyList());
        Assert.assertFalse(appCache.contains(query));
        appCache.put(Database.CONCERT_SLOTS_PATH, "0", slot1);
        appCache.put(Database.CONCERT_SLOTS_PATH, "1", slot2);
        query = new MyQuery(Database.CONCERT_SLOTS_PATH, Collections.emptyList());
        Assert.assertTrue(appCache.contains(query));
    }

    @Test
    public void containsCorrectForPerIdRetrieval() throws IOException {
        appCache.put(Database.USERS_PATH, karim.getUid(), karim);
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, karim.getUid()));
        Assert.assertTrue(appCache.contains(query));
        query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, axel.getUid()));
        Assert.assertFalse(appCache.contains(query));
    }

    @Test
    public void containsWithGeoclauseQueryFalse() throws IOException {
        appCache.put(Database.LOCATIONS_PATH, "0", Location.DEFAULT_LOCATION);
        MyQuery query = new MyQuery(Database.LOCATIONS_PATH, new MyGeoClause(0, 0, 1));
        Assert.assertFalse(appCache.contains(query));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void containsWithFieldWhereClauseFalse() throws IOException {
        appCache.put(Database.USERS_PATH, karim.getUid(), karim);
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause("email", EQUAL, karim.getEmail()));
        appCache.contains(query);
    }

    @Test
    public void putGetWorksCorrectlyWithCustomTypes() throws Throwable {
        appCache.put(Database.CONCERT_SLOTS_PATH, "0", slot1);
        appCache.put(Database.CONCERT_SLOTS_PATH, "1", slot2);

        MyQuery query = new MyQuery(Database.CONCERT_SLOTS_PATH, Collections.emptyList());
        assertCustomResults(Arrays.asList(slot1, slot2), appCache.get(query, Slot.class));

        query = new MyQuery(Database.CONCERT_SLOTS_PATH,
                    new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, "0"));
        assertCustomResults(Collections.singletonList(slot1), appCache.get(query, Slot.class));

    }

    @Test
    public void putGetWorksCorrectlyWithMapTypes() throws Throwable {
        Map<String, Object> f1 = new HashMap<>();
        f1.put("0", true);
        appCache.put(Database.FRIENDSHIPS_PATH, "1", f1);
        Map<String, Object> f2 = new HashMap<>();
        f2.put("1", true);
        appCache.put(Database.FRIENDSHIPS_PATH, "0", f2);

        MyQuery query = new MyQuery(Database.FRIENDSHIPS_PATH, Collections.emptyList());
        assertMapResults(Arrays.asList(f1, f2), appCache.get(query));

        query = new MyQuery(Database.FRIENDSHIPS_PATH,
                new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, "0"));
        assertMapResults(Collections.singletonList(f2), appCache.get(query));


    }

    @Test
    public void flushRemovesAllCache() throws Throwable {
        appCache.put(Database.USERS_PATH, "0", karim);
        appCache.put(Database.LOCATIONS_PATH, "1", Location.DEFAULT_LOCATION);
        appCache.put(Database.CONCERT_SLOTS_PATH, "2", slot1);
        appCache.put(Database.TOKENS_PATH, "3", token1);
        Assert.assertTrue(appCache.contains(new MyQuery(Database.USERS_PATH, Collections.emptyList())));
        Assert.assertTrue(appCache.contains(new MyQuery(Database.LOCATIONS_PATH, Collections.emptyList())));
        Assert.assertTrue(appCache.contains(new MyQuery(Database.CONCERT_SLOTS_PATH, Collections.emptyList())));
        Assert.assertTrue(appCache.contains(new MyQuery(Database.TOKENS_PATH, Collections.emptyList())));
        appCache.flush();
        Assert.assertFalse(appCache.contains(new MyQuery(Database.USERS_PATH, Collections.emptyList())));
        Assert.assertFalse(appCache.contains(new MyQuery(Database.LOCATIONS_PATH, Collections.emptyList())));
        Assert.assertFalse(appCache.contains(new MyQuery(Database.CONCERT_SLOTS_PATH, Collections.emptyList())));
        Assert.assertFalse(appCache.contains(new MyQuery(Database.TOKENS_PATH, Collections.emptyList())));

    }

    private <T> void assertCustomResults(List<T> expected, CompletableFuture<List<T>> actual) throws Throwable {
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

    private void assertMapResults(List<Map<String, Object>> expected,
                                  CompletableFuture<List<Map<String, Object>>> actual) throws Throwable{

        TestAsyncUtils sync = new TestAsyncUtils();
        actual.whenComplete((list, throwable) -> {
            if (throwable == null) {
                Log.d("TEST", "assertResults: " + expected.toString());
                Log.d("TEST", "assertResults: " + list.toString());
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
