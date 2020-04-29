package ch.epfl.balelecbud.util.database;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.emergency.models.EmergencyNumber;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestType;
import ch.epfl.balelecbud.schedule.models.Slot;

public class MockDatabaseWrapper implements DatabaseWrapper {
    public static final User karim =
            new User("karim@epfl.ch", "karim", MockAuthenticator.provideUid());
    public static final User celine =
            new User("celine@epfl.ch", "celine", MockAuthenticator.provideUid());
    public static final User alex =
            new User("alex@epfl.ch", "alex", MockAuthenticator.provideUid());
    public static final User axel =
            new User("axel@epfl.ch", "celine", MockAuthenticator.provideUid());
    public static final User gaspard =
            new User("gaspard@epfl.ch", "gaspard", MockAuthenticator.provideUid());
    public static final PointOfInterest pointOfInterest1 = new PointOfInterest(
            new Location(4, 20), "Bar IC", PointOfInterestType.BAR);
    public static final PointOfInterest pointOfInterest2 = new PointOfInterest(
            new Location(4, 22), "Bar EE", PointOfInterestType.BAR);
    private static final String TAG = MockDatabaseWrapper.class.getSimpleName();
    private static final MockDatabaseWrapper instance = new MockDatabaseWrapper();

    public static Slot slot1;
    public static Slot slot2;
    public static Slot slot3;
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Map<String, Boolean>> friendships = new HashMap<>();
    private final Map<String, Map<String, Boolean>> friendRequests = new HashMap<>();
    private final List<FestivalInformation> festivalInfos = new ArrayList<>();
    private final List<PointOfInterest> pointOfInterests = new ArrayList<>();
    private final List<EmergencyInfo> emergencyInfos = new ArrayList<>();
    private final Map<String, EmergencyNumber> emergencyNumbers = new HashMap<>();
    private final List<Slot> slots = new ArrayList<>();
    private final Map<String, Emergency> emergencies = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Consumer<Location>> friendsLocationListener = new HashMap<>();

    private final HashMap<String, HashMap<String, Object>> database = new HashMap<>();

    private MockDatabaseWrapper() {

        database.put(DatabaseWrapper.USERS_PATH, new HashMap<>());
        database.put(DatabaseWrapper.FRIENDSHIPS_PATH, new HashMap<>());
        database.put(DatabaseWrapper.FRIEND_REQUESTS_PATH, new HashMap<>());
        database.put(DatabaseWrapper.CONCERT_SLOTS_PATH, new HashMap<>());
        database.put(DatabaseWrapper.EMERGENCIES_PATH, new HashMap<>());
        database.put(DatabaseWrapper.EMERGENCY_INFO_PATH, new HashMap<>());
        database.put(DatabaseWrapper.EMERGENCY_NUMBER_PATH, new HashMap<>());
        database.put(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, new HashMap<>());
        database.put(DatabaseWrapper.LOCATIONS_PATH, new HashMap<>());
        database.put(DatabaseWrapper.POINT_OF_INTEREST_PATH, new HashMap<>());


        storeDocument(USERS_PATH, karim);
        storeDocument(USERS_PATH, celine);
        storeDocument(USERS_PATH, alex);
        storeDocument(USERS_PATH, axel);
        storeDocument(USERS_PATH, gaspard);
        List<Timestamp> timestamps = new LinkedList<>();
        for (int i = 0; i < 6; ++i) {
            Calendar c = Calendar.getInstance();
            c.set(2020, 11, 11, 10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3));
        slot3 = new Slot(2, "Upset", "Scène Sat'", timestamps.get(4), timestamps.get(5));
    }

    public static MockDatabaseWrapper getInstance() {
        return instance;
    }

    @Override
    public void unregisterDocumentListener(String collectionName, String documentID) {
        switch (collectionName) {
            case DatabaseWrapper.LOCATIONS_PATH:
                friendsLocationListener.remove(documentID);
                break;
            default:
                throw new IllegalArgumentException("MockDataBaseWrapper.unregisterDocumentListener()" +
                        " is not configure for collection = [" + collectionName + "]");
        }
    }

    @Override
    public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) {
        switch (collectionName) {
            case DatabaseWrapper.LOCATIONS_PATH:
                friendsLocationListener.put(documentID, (Consumer<Location>) consumer);
                ((Consumer<Location>) consumer).accept(locations.get(documentID));
                break;
            default:
                throw new IllegalArgumentException("MockDataBaseWrapper.listenDocument() is not configure" +
                        " for collection = [" + collectionName + "]");
        }
    }

    private List<Object> getCollectionItems(String collectionName) {
        return new ArrayList<>(database.get(collectionName).values());
    }

    @Override
    public <T> CompletableFuture<List<T>> queryWithType(MyQuery query, Class<T> tClass) {
        List<T> queryResult = new LinkedList<>();
        Map<String, Object> collection = database.get(query.getCollectionName());
        if (MockQueryUtils.queryContainsDocumentIdClause(query)) {
            T result = (T) collection.get(MockQueryUtils.getRightOperandFromDocumentIdClause(query));
            queryResult.add(result);
        } else {
            List<Object> collectionItems = getCollectionItems(query.getCollectionName());
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
        Map<String, Object> collection = database.get(query.getCollectionName());
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
            database.get(collectionName).put(user.getUid(), user);
            database.get(DatabaseWrapper.FRIEND_REQUESTS_PATH).put(user.getUid(), new HashMap<>());
            database.get(DatabaseWrapper.FRIENDSHIPS_PATH).put(user.getUid(), new HashMap<>());

        } else {
            database.get(collectionName).put(generateRandomID(), document);
        }
        Log.d(this.getClass().getSimpleName(), database.toString());
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        Log.d(TAG, "storeDocumentWithID() called with: collectionName = [" + collectionName + "], document = [" + document + "]");
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                throw new IllegalArgumentException("Cannot add user with custom identifier");
            case DatabaseWrapper.LOCATIONS_PATH:
                if (friendsLocationListener.containsKey(documentID))
                    friendsLocationListener.get(documentID).accept((Location) document);
                break;
            default:
                break;
        }
        database.get(collectionName).put(documentID, document);
        Log.d(this.getClass().getSimpleName(), database.toString());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
        for (String key : updates.keySet()) {
            if (updates.get(key).equals(FieldValue.delete())) {
                ((Map<String, Object>) database.get(collectionName).get(documentID)).remove(key);
            } else {
                ((Map<String, Object>) database.get(collectionName).get(documentID)).put(key, updates.get(key));
            }
        }
    }

    public void updateDocument(String collectionName, int documentID, Object update) {
        switch (collectionName) {
            case DatabaseWrapper.EMERGENCY_INFO_PATH:
                emergencyInfos.set(documentID, (EmergencyInfo) update);
        }
    }

    public void deleteDocument(String collectionName, Object document) {
        switch (collectionName) {
            case DatabaseWrapper.FESTIVAL_INFORMATION_PATH:
                festivalInfos.remove(document);
                break;
            case DatabaseWrapper.POINT_OF_INTEREST_PATH:
                pointOfInterests.remove(document);
                break;
            case DatabaseWrapper.EMERGENCY_INFO_PATH:
                emergencyInfos.remove(document);
            case DatabaseWrapper.EMERGENCY_NUMBER_PATH:
                emergencyNumbers.remove(document);
            case DatabaseWrapper.CONCERT_SLOTS_PATH:
                slots.remove(document);
                break;
            case DatabaseWrapper.EMERGENCIES_PATH:
                emergencies.remove(document);
                break;
            default:
                throw new IllegalArgumentException("unsupported collectionName" + collectionName);
        }
    }

    @Override
    public void deleteDocumentWithID(String collectionName, String documentID) {
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                users.remove(documentID);
                break;
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                friendships.remove(documentID);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                friendRequests.remove(documentID);
                break;
        }
    }

    public void resetFriendshipsAndRequests() {
        for (Map<String, Boolean> m : friendships.values()) {
            m.clear();
        }
        for (Map<String, Boolean> m : friendRequests.values()) {
            m.clear();
        }
        friendsLocationListener.clear();
    }

    public void resetDocument(String collectionName) {
        switch (collectionName) {
            case DatabaseWrapper.POINT_OF_INTEREST_PATH:
                pointOfInterests.clear();
                break;
            case DatabaseWrapper.LOCATIONS_PATH:
                locations.clear();
                break;
            case DatabaseWrapper.FESTIVAL_INFORMATION_PATH:
                festivalInfos.clear();
                break;
            case DatabaseWrapper.EMERGENCY_INFO_PATH:
                emergencyInfos.clear();
            case DatabaseWrapper.CONCERT_SLOTS_PATH:
                slots.clear();
                break;
            case DatabaseWrapper.EMERGENCIES_PATH:
                emergencies.clear();
                break;
            case DatabaseWrapper.EMERGENCY_NUMBER_PATH:
                emergencyNumbers.clear();
                break;
            default:
                throw new IllegalArgumentException("unsupported collectionName");
        }
    }



    public int getFriendsLocationListenerCount() {
        return this.friendsLocationListener.size();
    }


    public String generateRandomID() {
        return UUID.randomUUID().toString();
    }

}
