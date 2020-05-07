package ch.epfl.balelecbud.util.database;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FieldValue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.cache.Cache;

import static ch.epfl.balelecbud.util.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.util.database.Database.FESTIVAL_INFORMATION_PATH;
import static ch.epfl.balelecbud.util.database.Database.FRIENDSHIPS_PATH;
import static ch.epfl.balelecbud.util.database.Database.FRIEND_REQUESTS_PATH;
import static ch.epfl.balelecbud.util.database.Database.LOCATIONS_PATH;
import static ch.epfl.balelecbud.util.database.Database.Source.CACHE;
import static ch.epfl.balelecbud.util.database.Database.Source.REMOTE;
import static ch.epfl.balelecbud.util.database.MockDatabase.assertQueryMapResults;
import static ch.epfl.balelecbud.util.database.MockDatabase.assertQueryResults;
import static ch.epfl.balelecbud.util.database.MockDatabase.karim;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;

@RunWith(AndroidJUnit4.class)
public class CachedDatabaseTest {

    private MockDatabase remoteDatabase;
    private CachedDatabase appDatabase;
    Cache appCache;

    private FestivalInformation freshInfo = new FestivalInformation("Information", "Fresh information");
    private FestivalInformation staleInfo = new FestivalInformation("Information", "Stale information");
    private Map<String, Object> freshFriend = new HashMap<>();
    private Map<String, Object> staleFriend = new HashMap<>();

    @Before
    public void setup() throws IOException {
        remoteDatabase = MockDatabase.getInstance();
        appDatabase = CachedDatabase.getInstance();
        appCache = BalelecbudApplication.getAppCache();
        BalelecbudApplication.setRemoteDatabase(remoteDatabase);
        BalelecbudApplication.setAppDatabase(appDatabase);

        freshFriend.put("11", true);
        staleFriend.put("10", true);

        remoteDatabase.resetDatabase();
        remoteDatabase.storeDocumentWithID(FESTIVAL_INFORMATION_PATH, "information", freshInfo);
        remoteDatabase.storeDocumentWithID(FRIENDSHIPS_PATH, karim.getUid(), freshFriend);

        appCache.flush();
        appCache.put(FESTIVAL_INFORMATION_PATH, "information", staleInfo);
        appCache.put(FRIENDSHIPS_PATH, karim.getUid(), staleFriend);
    }

    @Test
    public void getDocumentsForceRemote() throws Throwable {
        MyQuery query = new MyQuery(FESTIVAL_INFORMATION_PATH, Collections.emptyList(), REMOTE);
        assertResults(Collections.singletonList(freshInfo),
                appDatabase.query(query, FestivalInformation.class));

        query = new MyQuery(FRIENDSHIPS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, karim.getUid()), REMOTE);
        assertMapResults(Collections.singletonList(freshFriend),
                appDatabase.query(query));
    }

    @Test
    public void getDocumentForceCache() throws Throwable {
        MyQuery query = new MyQuery(FESTIVAL_INFORMATION_PATH, Collections.emptyList(), CACHE);
        assertResults(Collections.singletonList(staleInfo),
                appDatabase.query(query, FestivalInformation.class));

        query = new MyQuery(FRIENDSHIPS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, karim.getUid()), CACHE);
        assertMapResults(Collections.singletonList(staleFriend),
                appDatabase.query(query));
    }

    @Test
    public void allOperationsExceptQueryGoToRemote() throws Throwable{
        appDatabase.listenDocument(LOCATIONS_PATH, "0", location -> {}, Location.class);
        Assert.assertEquals(1, remoteDatabase.getFriendsLocationListenerCount());
        appDatabase.unregisterDocumentListener(LOCATIONS_PATH, "0");
        Assert.assertEquals(0, remoteDatabase.getFriendsLocationListenerCount());

        MyQuery query = new MyQuery(LOCATIONS_PATH, Collections.emptyList());

        appDatabase.storeDocumentWithID(LOCATIONS_PATH, "defaultLocation" ,Location.DEFAULT_LOCATION);
        assertResults(Collections.singletonList(Location.DEFAULT_LOCATION),
                remoteDatabase.query(query, Location.class));

        appDatabase.deleteDocumentWithID(LOCATIONS_PATH, "defaultLocation");
        assertResults(Collections.emptyList(),
                remoteDatabase.query(query, Location.class));

        appDatabase.storeDocument(LOCATIONS_PATH, Location.DEFAULT_LOCATION);
        assertResults(Collections.singletonList(Location.DEFAULT_LOCATION),
                remoteDatabase.query(query, Location.class));

        query = new MyQuery(FRIEND_REQUESTS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, karim.getUid()));
        appDatabase.storeDocumentWithID(FRIEND_REQUESTS_PATH, karim.getUid(), new HashMap<String, Boolean>() {{ put("friend", true); }});
        appDatabase.updateDocument(FRIEND_REQUESTS_PATH, karim.getUid(), new HashMap<String, Object>() {{ put("friend", FieldValue.delete()); }});
        assertResults(Collections.singletonList(Collections.emptyMap()), appDatabase.query(query));

    }

    private <T> void assertResults(List<T> expected, CompletableFuture<List<T>> actual) throws Throwable{
        TestAsyncUtils sync = new TestAsyncUtils();
        assertQueryResults(sync, expected, actual);
    }

    private void assertMapResults(List<Map<String, Object>> expected,
                                  CompletableFuture<List<Map<String, Object>>> actual) throws Throwable{
        TestAsyncUtils sync = new TestAsyncUtils();
        assertQueryMapResults(sync, expected, actual);
    }

}
