package ch.epfl.balelecbud.util.cache;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import static ch.epfl.balelecbud.util.database.MockDatabase.assertQueryMapResults;
import static ch.epfl.balelecbud.util.database.MockDatabase.assertQueryResults;
import static ch.epfl.balelecbud.util.database.MockDatabase.axel;
import static ch.epfl.balelecbud.util.database.MockDatabase.karim;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot1;
import static ch.epfl.balelecbud.util.database.MockDatabase.slot2;
import static ch.epfl.balelecbud.util.database.MockDatabase.token1;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;

@RunWith(AndroidJUnit4.class)
public class FilesystemCacheTest {

    private final Cache appCache = BalelecbudApplication.getAppCache();

    @Before
    public void setup() {
        BalelecbudApplication.setRemoteDatabase(MockDatabase.getInstance());
        appCache.flush();
    }

    @Test
    public void testContainsForCollectionRetrieval() throws IOException {
        MyQuery query = new MyQuery(Database.POINT_OF_INTEREST_PATH, Collections.emptyList());
        Assert.assertFalse(appCache.contains(query));
        appCache.put(Database.CONCERT_SLOTS_PATH, "0", slot1);
        appCache.put(Database.CONCERT_SLOTS_PATH, "1", slot2);
        query = new MyQuery(Database.CONCERT_SLOTS_PATH, Collections.emptyList());
        Assert.assertTrue(appCache.contains(query));
    }

    @Test
    public void testContainsForPerIdRetrieval() throws IOException {
        appCache.put(Database.USERS_PATH, karim.getUid(), karim);
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, karim.getUid()));
        Assert.assertTrue(appCache.contains(query));
        query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, axel.getUid()));
        Assert.assertFalse(appCache.contains(query));
    }

    @Test
    public void testContainsWithGeoclauseQuery() throws IOException {
        appCache.put(Database.LOCATIONS_PATH, "0", Location.DEFAULT_LOCATION);
        MyQuery query = new MyQuery(Database.LOCATIONS_PATH, new MyGeoClause(0, 0, 1));
        Assert.assertFalse(appCache.contains(query));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testContainsWithFieldWhereClause() throws IOException {
        appCache.put(Database.USERS_PATH, karim.getUid(), karim);
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause("email", EQUAL, karim.getEmail()));
        appCache.contains(query);
    }

    @Test
    public void testPutAndGetWithCustomTypes() throws Throwable {
        appCache.put(Database.CONCERT_SLOTS_PATH, "0", slot1);
        appCache.put(Database.CONCERT_SLOTS_PATH, "1", slot2);

        TestAsyncUtils sync = new TestAsyncUtils();
        MyQuery query = new MyQuery(Database.CONCERT_SLOTS_PATH, Collections.emptyList());
        assertQueryResults(sync, Arrays.asList(slot1, slot2), appCache.get(query, Slot.class));

        sync = new TestAsyncUtils();
        query = new MyQuery(Database.CONCERT_SLOTS_PATH,
                    new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, "0"));
        assertQueryResults(sync, Collections.singletonList(slot1), appCache.get(query, Slot.class));

    }

    @Test
    public void testPutAndGetWithMapTypes() throws Throwable {
        Map<String, Object> f1 = new HashMap<>();
        f1.put("0", true);
        appCache.put(Database.FRIENDSHIPS_PATH, "1", f1);
        Map<String, Object> f2 = new HashMap<>();
        f2.put("1", true);
        appCache.put(Database.FRIENDSHIPS_PATH, "0", f2);

        TestAsyncUtils sync = new TestAsyncUtils();
        MyQuery query = new MyQuery(Database.FRIENDSHIPS_PATH, Collections.emptyList());
        assertQueryMapResults(sync, Arrays.asList(f1, f2), appCache.get(query));

        sync = new TestAsyncUtils();
        query = new MyQuery(Database.FRIENDSHIPS_PATH,
                new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, "0"));
        assertQueryMapResults(sync, Collections.singletonList(f2), appCache.get(query));


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
}
