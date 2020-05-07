package ch.epfl.balelecbud.util.database;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestType;
import ch.epfl.balelecbud.schedule.models.Slot;

@SuppressWarnings("ALL")
public class MockDatabase implements Database {
    public static final User karim =
            new User("karim@epfl.ch", "karim", MockAuthenticator.provideUid());
    public static final User camille =
            new User("camille@epfl.ch", "camille", MockAuthenticator.provideUid());
    public static final User celine =
            new User("celine@epfl.ch", "celine", MockAuthenticator.provideUid());
    public static final User alex =
            new User("alex@epfl.ch", "alex", MockAuthenticator.provideUid());
    public static final User axel =
            new User("axel@epfl.ch", "axel", MockAuthenticator.provideUid());
    public static final User gaspard =
            new User("gaspard@epfl.ch", "gaspard", MockAuthenticator.provideUid());
    public static final PointOfInterest pointOfInterest1 = new PointOfInterest(
            new Location(4, 20), "Bar IC", PointOfInterestType.BAR);
    public static final PointOfInterest pointOfInterest2 = new PointOfInterest(
            new Location(4, 22), "Bar EE", PointOfInterestType.BAR);


    public static final String token1 = "TheBestTestToken";
    public static final String token2 = "TheNormalTestToken";

    private static final String TAG = MockDatabase.class.getSimpleName();
    private static final MockDatabase instance = new MockDatabase();

    public static Slot slot1;
    public static Slot slot2;
    public static Slot slot3;

    private final Map<String, Consumer<Location>> friendsLocationListener = new HashMap<>();

    private Map<String, Map<String, Object>> databasePOJO;
    private Map<String, Map<String, Map<String, Boolean>>> database;

    private MockDatabase() {
        resetDatabase();
    }

    public void resetDatabase() {
        initDatabasePOJO();
        initDatabase();
        fillDB();
    }

    private void initDatabasePOJO() {
        databasePOJO = new HashMap<>();
        databasePOJO.put(Database.USERS_PATH, new LinkedHashMap<>());
        databasePOJO.put(Database.CONCERT_SLOTS_PATH, new LinkedHashMap<>());
        databasePOJO.put(Database.EMERGENCIES_PATH, new LinkedHashMap<>());
        databasePOJO.put(Database.EMERGENCY_INFO_PATH, new LinkedHashMap<>());
        databasePOJO.put(Database.EMERGENCY_NUMBER_PATH, new LinkedHashMap<>());
        databasePOJO.put(Database.FESTIVAL_INFORMATION_PATH, new LinkedHashMap<>());
        databasePOJO.put(Database.LOCATIONS_PATH, new LinkedHashMap<>());
        databasePOJO.put(Database.POINT_OF_INTEREST_PATH, new LinkedHashMap<>());
    }

    private void initDatabase() {
        database = new HashMap<>();
        database.put(Database.FRIENDSHIPS_PATH, new LinkedHashMap<>());
        database.put(Database.FRIEND_REQUESTS_PATH, new LinkedHashMap<>());
        database.put(Database.SENT_REQUESTS_PATH, new LinkedHashMap<>());
        database.put(Database.TOKENS_PATH, new LinkedHashMap<>());
    }

    private void fillDB() {
        storeDocument(USERS_PATH, karim);
        storeDocument(USERS_PATH, celine);
        storeDocument(USERS_PATH, alex);
        storeDocument(USERS_PATH, axel);
        storeDocument(USERS_PATH, gaspard);
        storeDocument(USERS_PATH, camille);


        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(token1, true);
        storeDocumentWithID(Database.TOKENS_PATH, celine.getUid(), toStore);
        toStore = new HashMap<>();
        toStore.put(token2, true);
        storeDocumentWithID(Database.TOKENS_PATH, karim.getUid(), toStore);

        List<Timestamp> timestamps = new LinkedList<>();
        for (int i = 0; i < 6; ++i) {
            Calendar c = Calendar.getInstance();
            c.set(2020, 11, 11, 10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", "path1", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", "path2", timestamps.get(2), timestamps.get(3));
        slot3 = new Slot(2, "Upset", "Scène Sat'", "path3", timestamps.get(4), timestamps.get(5));
    }

    public static MockDatabase getInstance() {
        return instance;
    }

    @Override
    public void unregisterDocumentListener(String collectionName, String documentID) {
        switch (collectionName) {
            case Database.LOCATIONS_PATH:
                friendsLocationListener.remove(documentID);
                break;
            default:
                throw new IllegalArgumentException("MockDataBaseWrapper.unregisterDocumentListener()" +
                        " is not configured for collection = [" + collectionName + "]");
        }
    }

    @Override
    public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) {
        switch (collectionName) {
            case Database.LOCATIONS_PATH:
                friendsLocationListener.put(documentID, (Consumer<Location>) consumer);
                ((Consumer<Location>) consumer).accept( (Location) databasePOJO.get(collectionName).get(documentID));
                break;
            default:
                throw new IllegalArgumentException("MockDataBaseWrapper.listenDocument() is not configured" +
                        " for collection = [" + collectionName + "]");
        }
    }

    @Override
    public <T> CompletableFuture<List<T>> queryWithType(MyQuery query, Class<T> tClass) {
        List<T> queryResult = new LinkedList<>();
        Map<String, Object> collection = databasePOJO.get(query.getCollectionName());
        if (MockQueryUtils.queryContainsDocumentIdClause(query)) {
            T result = (T) collection.get(MockQueryUtils.getRightOperandFromDocumentIdClause(query));
            queryResult.add(result);
        } else {
            List<Object> collectionItems = new  ArrayList<>(databasePOJO.get(query.getCollectionName()).values());
            for (Object elem : collectionItems) {
                queryResult.add(tClass.cast(elem));
            }
            for (MyWhereClause clause : query.getWhereClauses()) {
                queryResult = MockQueryUtils.filterList(queryResult, clause);
            }
        }
        return CompletableFuture.completedFuture(queryResult);
    }


    @Override
    public CompletableFuture<List<Map<String, Object>>> query(MyQuery query) {
        List<Map<String, Object>> queryResult = new LinkedList<>();
        Map<String, Map<String, Boolean>> collection = database.get(query.getCollectionName());
        if (MockQueryUtils.queryContainsDocumentIdClause(query)) {
            Object result = collection.get(MockQueryUtils.getRightOperandFromDocumentIdClause(query));
            queryResult.add((Map<String, Object>) result);
            return CompletableFuture.completedFuture(queryResult);
        } else {
            throw new UnsupportedOperationException("This type of query is not supported yet.");
        }
    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
        Log.d(TAG, "storeDocument() called with: collectionName = [" + collectionName + "], document = [" + document + "]");
        if (document instanceof User) {
            User user = (User) document;
            databasePOJO.get(collectionName).put(user.getUid(), user);
            database.get(Database.FRIEND_REQUESTS_PATH).put(user.getUid(), new LinkedHashMap<>());
            database.get(Database.FRIENDSHIPS_PATH).put(user.getUid(), new LinkedHashMap<>());
            database.get(Database.SENT_REQUESTS_PATH).put(user.getUid(), new LinkedHashMap<>());
            database.get(Database.TOKENS_PATH).put(user.getUid(), new LinkedHashMap<>());
        } else {
            databasePOJO.get(collectionName).put(generateRandomID(), document);
        }
        logContents();
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        Log.d(TAG, "storeDocumentWithID() called with: collectionName = [" + collectionName + "], document = [" + document + "]");
        switch (collectionName) {
            case Database.FRIEND_REQUESTS_PATH:
            case Database.FRIENDSHIPS_PATH:
            case Database.SENT_REQUESTS_PATH:
            case Database.TOKENS_PATH:
                database.get(collectionName).get(documentID).putAll((Map<String, Boolean>)document);
                break;
            case Database.LOCATIONS_PATH:
                databasePOJO.get(collectionName).put(documentID, document);
                if (friendsLocationListener.containsKey(documentID))
                    friendsLocationListener.get(documentID).accept((Location) document);
                break;
            case Database.USERS_PATH:
                User user = (User) document;
                if (user.getUid() != documentID)
                    throw new IllegalArgumentException("Cannot add user with custom identifier");
                storeDocument(collectionName, document);
            default:
                databasePOJO.get(collectionName).put(documentID, document);
                break;
        }
        logContents();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
        switch (collectionName) {
            case Database.FRIEND_REQUESTS_PATH:
            case Database.FRIENDSHIPS_PATH:
            case Database.SENT_REQUESTS_PATH:
            case Database.TOKENS_PATH:
                for (String key : updates.keySet()) {
                    if (updates.get(key).equals(FieldValue.delete())) {
                        database.get(collectionName).get(documentID).remove(key);
                    } else {
                        database.get(collectionName).get(documentID).put(key, (Boolean) updates.get(key));
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Cannot update custom POJO documents");
        }
        logContents();
    }

    @Override
    public void deleteDocumentWithID(String collectionName, String documentID) {
        switch (collectionName) {
            case Database.FRIENDSHIPS_PATH:
            case Database.FRIEND_REQUESTS_PATH:
            case Database.SENT_REQUESTS_PATH:
            case Database.TOKENS_PATH:
                throw new UnsupportedOperationException();
            default:
                databasePOJO.get(collectionName).remove(documentID);
                break;
        }
        logContents();
    }

    public void resetDocument(String collectionName) {
        switch (collectionName) {
            case Database.FRIEND_REQUESTS_PATH:
            case Database.FRIENDSHIPS_PATH:
            case Database.SENT_REQUESTS_PATH:
            case Database.TOKENS_PATH:
                for (Map o : database.get(collectionName).values()) {
                    o.clear();
                }
                friendsLocationListener.clear();
                break;
            default:
                databasePOJO.get(collectionName).clear();
        }
        logContents();
    }

    public int getFriendsLocationListenerCount() {
        return this.friendsLocationListener.size();
    }

    public String generateRandomID() {
        return UUID.randomUUID().toString();
    }

    private void logContents() {
        Log.d(TAG, databasePOJO.toString() + "\n" + database.toString() + "\n" + friendsLocationListener.toString());
    }

}