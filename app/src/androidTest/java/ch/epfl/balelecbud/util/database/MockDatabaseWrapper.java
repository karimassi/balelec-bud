package ch.epfl.balelecbud.util.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import org.junit.Assert;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.schedule.models.Slot;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;

public class MockDatabaseWrapper implements DatabaseWrapper {
    private static final String TAG = MockDatabaseWrapper.class.getSimpleName();

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

    private static final MockDatabaseWrapper instance = new MockDatabaseWrapper();

    public static Slot slot1;
    public static Slot slot2;
    public static Slot slot3;
    private final List<DatabaseListener> listeners = new ArrayList<>();
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Map<String, Boolean>> friendships = new HashMap<>();
    private final Map<String, Map<String, Boolean>> friendRequests = new HashMap<>();
    private final List<FestivalInformation> festivalInfos = new ArrayList<>();
    private final List<PointOfInterest> pointOfInterests = new ArrayList<>();
    private final List<Slot> slots = new ArrayList<>();
    private final Map<String, Emergency> emergencies = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Consumer<Location>> friendsLocationListener = new HashMap<>();

    private MockDatabaseWrapper() {
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
    public void unregisterListener(DatabaseListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void listen(String collectionName, DatabaseListener listener) {
        listeners.add(listener);
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
                if (locations.containsKey(documentID)) {
                    ((Consumer<Location>) consumer).accept(locations.get(documentID));
                }
                break;
            default:
                throw new IllegalArgumentException("MockDataBaseWrapper.listenDocument() is not configure" +
                        " for collection = [" + collectionName + "]");
        }
    }

    @Override
    public <T> CompletableFuture<List<T>> query(MyQuery query, Class<T> tClass) {
        List<T> list = new LinkedList<>();
        List<T> listToQuery = getListToQuery(query.getCollectionName());
        for (Object elem : listToQuery) {
            list.add(tClass.cast(elem));
        }
        for (MyWhereClause clause : query.getWhereClauses()) {
            list = filterList(list, clause);
        }
        return CompletableFuture.completedFuture(list);
    }

    private List getListToQuery(String name) {
        switch (name) {
            case DatabaseWrapper.FESTIVAL_INFORMATION_PATH:
                return festivalInfos;
            case DatabaseWrapper.POINT_OF_INTEREST_PATH:
                return pointOfInterests;
            case DatabaseWrapper.LOCATIONS_PATH:
                return new LinkedList(locations.values());
            case DatabaseWrapper.CONCERT_SLOTS_PATH:
                return slots;
            case DatabaseWrapper.EMERGENCIES_PATH:
                return new LinkedList(emergencies.values());
            default:

                throw new IllegalArgumentException("Unsupported collection name " + name);
        }
    }

    private <A> List<A> filterList(List<A> list, MyWhereClause clause) {
        List<A> newList = new LinkedList<>();
        if (list.isEmpty())
            return list;
        A head = list.get(0);
        Class clazz = head.getClass();
        Field field = getField(clause, clazz);
        Object rightOperand = clause.getRightOperand();
        // check that the field and our value have the same type,
        // right now they must be exactly the same class
        if (!rightOperand.getClass().equals(field.getType())) {
            return newList;
        }
        for (A elem : list) {
            filterELem(clause.getOp(), newList, field, rightOperand, elem);
        }
        return newList;
    }

    private <A> void filterELem(MyWhereClause.Operator op, List<A> newList, Field field, Object rightOperand, A elem) {
        try {
            if (op == MyWhereClause.Operator.EQUAL) {
                if (Objects.equals(field.get(elem), rightOperand)) {
                    newList.add(elem);
                }
            } else if (field.get(elem) instanceof Comparable) {
                Comparable rightOperandComparable = (Comparable) field.get(elem);
                // fine because we checked before that the types were equal, would be nice
                // to find a way so that the IDE doesn't complain
                int result = rightOperandComparable.compareTo(rightOperand);
                if (evaluateResult(op, result)) {
                    newList.add(elem);
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Given field param cannot be accessed");
        }
    }

    @NonNull
    private Field getField(MyWhereClause clause, Class clazz) {
        Field field;
        try {
            field = clazz.getDeclaredField(clause.getLeftOperand());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Class" + clazz.getName() + "does not contain a" + clause.getLeftOperand());
        }
        // to allow us to modify variable
        field.setAccessible(true);
        return field;
    }

    private boolean evaluateResult(MyWhereClause.Operator op, int resultOfComparison) {
        switch (op) {
            case LESS_THAN:
                return resultOfComparison < 0;
            case LESS_EQUAL:
                return resultOfComparison <= 0;
            case GREATER_THAN:
                return resultOfComparison > 0;
            case GREATER_EQUAL:
                return resultOfComparison >= 0;
            default:
                return resultOfComparison == 0;
        }
    }

    @Override
    public CompletableFuture<List<String>> queryIds(MyQuery query) {
        Map<String, Map<String, Boolean>> mapToQuery = getMapToQuery(query.getCollectionName());
        List<Map<String, Boolean>> filteredValues = filterMapValues(mapToQuery, query.getWhereClauses());
        Set<String> filteredKeys = filterKeys(mapToQuery, filteredValues);
        return CompletableFuture.completedFuture(new LinkedList<>(filteredKeys));
    }

    private Map<String, Map<String, Boolean>> getMapToQuery(String name) {
        switch (name) {
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                return friendRequests;
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                return friendships;
            default:
                throw new IllegalArgumentException("Unsupported collection name " + name);
        }
    }

    private List<Map<String, Boolean>> filterMapValues(Map<String, Map<String, Boolean>> mapToFilter, List<MyWhereClause> clauses) {
        List<Map<String, Boolean>> values = new LinkedList<>(mapToFilter.values());
        for (MyWhereClause clause : clauses) {
            values = values.stream().filter(x -> x.getOrDefault(clause.getLeftOperand(), false)).collect(Collectors.toList());
        }
        return values;
    }

    private Set<String> filterKeys(Map<String, Map<String, Boolean>> mapToFilter, List<Map<String, Boolean>> valuesToKeep) {
        Set<String> filteredKeys = new HashSet<>();
        for (String key : mapToFilter.keySet()) {
            if (valuesToKeep.contains(mapToFilter.get(key))) {
                filteredKeys.add(key);
            }
        }
        return filteredKeys;
    }

    public void addItem(final Object object) throws Throwable {
        runOnUiThreadWithLog("addItem", object, () -> addItemAux(object));
    }

    public void modifyItem(final Object object, final int index) throws Throwable {
        runOnUiThreadWithLog("modifyItem", object, () -> changeItemAux(object, index));
    }

    public void removeItem(final Object object, final int index) throws Throwable {
        runOnUiThreadWithLog("removeItem", object, () -> removeItemAux(object, index));
    }

    private void runOnUiThreadWithLog(String functionName, Object object, Runnable runnable) throws Throwable {
        Log.d(TAG, functionName + "() called with: object = [" + object + "]");
        runOnUIThreadAndWait(runnable);
    }

    private void addItemAux(Object item) {
        for (DatabaseListener l : listeners) {
            l.onItemAdded(item);
        }
    }

    private void changeItemAux(Object item, int position) {
        for (DatabaseListener l : listeners) {
            l.onItemChanged(item, position);
        }
    }

    private void removeItemAux(Object item, int position) {
        for (DatabaseListener l : listeners) {
            l.onItemRemoved(item, position);
        }
    }

    @Override
    public <T> CompletableFuture<T> getCustomDocument(String collectionName, String documentID, Class<T> type) {
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                return CompletableFuture.completedFuture((T) users.get(documentID));
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                return CompletableFuture.completedFuture((T) friendships.get(documentID));
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                return CompletableFuture.completedFuture((T) friendRequests.get(documentID));
            case DatabaseWrapper.LOCATIONS_PATH:
                return CompletableFuture.completedFuture((T) locations.get(documentID));
            case DatabaseWrapper.EMERGENCIES_PATH:
                return CompletableFuture.completedFuture((T) emergencies.get(documentID));
            default:
                return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> getDocument(String collectionName, String documentID) {
        Map<String, Object> result = new HashMap<>();
        switch (collectionName) {
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                result.putAll(friendships.get(documentID));
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                result.putAll(friendRequests.get(documentID));
                break;
            default:
                throw new IllegalArgumentException("Unsupported collectionName " + collectionName);
        }
        return CompletableFuture.completedFuture(result);
    }


    @Override
    public <T> CompletableFuture<T> getDocumentWithFieldCondition(String collectionName, String fieldName,
                                                                  String fieldValue, Class<T> type) {
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                for (User u : users.values()) {
                    if ("email".equals(fieldName)) {
                        if (u.getEmail().equals(fieldValue)) {
                            return CompletableFuture.completedFuture((T) u);
                        }
                    }
                }

                break;
            case DatabaseWrapper.EMERGENCIES_PATH:
                for (Emergency u : emergencies.values()) {

                    if ("category".equals(fieldName)) {
                        if (u.getCategory().toString().equals(fieldValue)) {
                            return CompletableFuture.completedFuture((T) u);
                        }
                    }
                }
                break;
            default:
        }

        return CompletableFuture.completedFuture(null);

    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
        Log.d(TAG, "storeDocument() called with: collectionName = [" + collectionName + "], document = [" + document + "]");
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                User newUser = (User) document;
                users.put(newUser.getUid(), newUser);
                friendRequests.put(newUser.getUid(), new HashMap<>());
                friendships.put(newUser.getUid(), new HashMap<>());
                break;
            case DatabaseWrapper.POINT_OF_INTEREST_PATH:
                pointOfInterests.add((PointOfInterest) document);
                break;
            case DatabaseWrapper.FESTIVAL_INFORMATION_PATH:
                festivalInfos.add((FestivalInformation) document);
                break;
            case DatabaseWrapper.CONCERT_SLOTS_PATH:
                slots.add((Slot) document);
                break;
            case DatabaseWrapper.EMERGENCIES_PATH:
                emergencies.put(generateRandomUid(), (Emergency) document);
                break;
            default:
                throw new IllegalArgumentException("Unsupported collection name " + collectionName);
        }
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                User newUser = (User) document;
                Assert.assertEquals("documentID must be equals to the userID", documentID, newUser.getUid());
                users.put(newUser.getUid(), newUser);
                friendRequests.put(newUser.getUid(), new HashMap<>());
                friendships.put(newUser.getUid(), new HashMap<>());
                break;
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                friendships.get(documentID).putAll((Map<String, Boolean>) document);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                friendRequests.get(documentID).putAll((Map<String, Boolean>) document);
                break;
            case DatabaseWrapper.LOCATIONS_PATH:
                locations.put(documentID, (Location) document);
                if (friendsLocationListener.containsKey(documentID))
                    friendsLocationListener.get(documentID).accept((Location) document);
                break;
            default:
                storeDocument(collectionName, document);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
        switch (collectionName) {
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                friendships.get(documentID).clear();
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                friendRequests.get(documentID).remove(new ArrayList<>(updates.keySet()).get(0));
                break;
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
        for (Map m : friendships.values()) {
            m.clear();
        }
        for (Map m : friendRequests.values()) {
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
            case DatabaseWrapper.CONCERT_SLOTS_PATH:
                slots.clear();
                break;
            case DatabaseWrapper.EMERGENCIES_PATH:
                emergencies.clear();
                break;
            default:
                throw new IllegalArgumentException("unsupported collectionName");
        }
    }

    public String generateRandomUid() {
        byte[] array = new byte[20]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        return generatedString;
    }

    public int getFriendsLocationListenerCount() {
        return this.friendsLocationListener.size();
    }
}
