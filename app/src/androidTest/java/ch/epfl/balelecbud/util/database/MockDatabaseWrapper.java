package ch.epfl.balelecbud.util.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import org.junit.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.schedule.models.Slot;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;

public class MockDatabaseWrapper implements DatabaseWrapper {
    private static final String TAG = MockDatabaseWrapper.class.getSimpleName();
    public static final User karim =
            new User("karim@epfl.ch", "karim", MockAuthenticator.provideUid());
    public static final User celine =
            new User("celine@epfl.ch", "celine", MockAuthenticator.provideUid());

    public static Slot slot1;
    public static Slot slot2;
    public static Slot slot3;

    private final List<DatabaseListener> listeners = new ArrayList<>();

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Map<String, Boolean>> friendships = new HashMap<>();
    private final Map<String, Map<String, Boolean>> friendRequests = new HashMap<>();
    private final List<FestivalInformation> festivalInfos = new ArrayList<>();
    private final List<PointOfInterest> pointOfInterests = new ArrayList<>();
    private final Map<String, Location> locations = new HashMap<>();

    private MockDatabaseWrapper() {
        users.put(karim.getUid(), karim);
        users.put(celine.getUid(), celine);
        friendships.put(karim.getUid(), new HashMap<>());
        friendships.put(celine.getUid(), new HashMap<>());
        friendRequests.put(karim.getUid(), new HashMap<>());
        friendRequests.put(celine.getUid(), new HashMap<>());
        List<Timestamp> timestamps = new LinkedList<>();
        for(int i = 0; i < 6; ++i){
            Calendar c = Calendar.getInstance();
            c.set(2020,11,11,10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3)) ;
        slot3 = new Slot(2, "Upset", "Scène Sat'",  timestamps.get(4), timestamps.get(5));
    }

    private static final MockDatabaseWrapper instance = new MockDatabaseWrapper();

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
    public <T> CompletableFuture<List<T>> query(MyQuery query, Class<T> tClass) {
        List<T> list = new LinkedList<>();
        List<T> listToQuery = getListToQuery(query.getCollectionName());
        for(Object elem : listToQuery){
            list.add(tClass.cast(elem));
        }
        for(MyQuery.WhereClause clause : query.getWhereClauses()){
            list = filterList(list, clause);
        }
        return CompletableFuture.completedFuture(list);
    }

    private List getListToQuery(String name){
        switch(name){
            case DatabaseWrapper.FESTIVAL_INFORMATION_PATH :
                return festivalInfos;
            case DatabaseWrapper.POINT_OF_INTEREST_PATH :
                return pointOfInterests;
            case DatabaseWrapper.LOCATIONS_PATH:
                return new LinkedList(locations.values());
            default :
                throw new IllegalArgumentException("Unsupported collection name " + name);
        }
    }
    //highly commented because not everybody is familiar with reflection
    private <A> List<A> filterList(List<A> list, MyQuery.WhereClause clause) {
        List<A> newList = new LinkedList<>();
        if(list.isEmpty())
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

    private <A> void filterELem(MyQuery.WhereClause.Operator op, List<A> newList, Field field, Object rightOperand, A elem) {
        try {
            if (op == MyQuery.WhereClause.Operator.EQUAL) {
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
    private Field getField(MyQuery.WhereClause clause, Class clazz) {
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

    private boolean evaluateResult(MyQuery.WhereClause.Operator op, int resultOfComparison){
        switch (op){
            case LESS_THAN: return resultOfComparison < 0;
            case LESS_EQUAL: return resultOfComparison <= 0;
            case GREATER_THAN: return resultOfComparison > 0;
            case GREATER_EQUAL: return resultOfComparison >= 0;
            default : return resultOfComparison == 0;
        }
    }

    public void addItem(final Object object) throws Throwable {
        Log.d(TAG, "addItem() called with: object = [" + object + "]");
        runOnUIThreadAndWait(() -> addItemAux(object));
    }

    public void modifyItem(final Object object, final int index) throws Throwable {
        Log.d(TAG, "modifyItem() called with: object = [" + object + "], index = [" + index + "]");
        runOnUIThreadAndWait(() -> changeItemAux(object, index));
    }

    public void removeItem(final Object object, final int index) throws Throwable {
        Log.d(TAG, "removeItem() called with: object = [" + object + "], index = [" + index + "]");
        runOnUIThreadAndWait(() -> removeItemAux(object, index));
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
    public <T> CompletableFuture<T> getDocumentWithFieldCondition(String collectionName, String fieldName, String fieldValue, Class<T> type) {
        if (DatabaseWrapper.USERS_PATH.equals(collectionName)) {
            for (User u : users.values()) {
                if ("email".equals(fieldName)) {
                    if (u.getEmail().equals(fieldValue)) {
                        return CompletableFuture.completedFuture((T) u);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
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
            default :
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
    }

    public void resetDocument(String collectionName){
        switch(collectionName){
            case DatabaseWrapper.POINT_OF_INTEREST_PATH:
                pointOfInterests.clear();
                break;
            case DatabaseWrapper.LOCATIONS_PATH:
                locations.clear();
                break;
            case DatabaseWrapper.FESTIVAL_INFORMATION_PATH:
                festivalInfos.clear();
                break;
            default :
                throw new IllegalArgumentException("unsupported collectionName");
        }
    }

}
