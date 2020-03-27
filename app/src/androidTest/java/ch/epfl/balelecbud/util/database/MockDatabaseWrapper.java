package ch.epfl.balelecbud.util.database;

import java.lang.reflect.Field;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class MockDatabaseWrapper implements DatabaseWrapper {

    private List<DatabaseListener> listeners;
    private List<Object> listToQuery;

    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Map<String, Boolean>> friendships = new ArrayList<>();
    private ArrayList<Map<String, Boolean>> friendRequests = new ArrayList<>();
    private Map<String, Location> locations = new HashMap<>();

    private MockDatabaseWrapper() {
        listeners = new ArrayList<>();
        listToQuery = new ArrayList<>();
        users.add(new User("karim@epfl.ch", "karim@epfl.ch", MockAuthenticator.provideUid()));
        users.add(new User("celine@epfl.ch", "celine@epfl.ch", MockAuthenticator.provideUid()));
        friendships.add(new HashMap<String, Boolean>());
        friendships.add(new HashMap<String, Boolean>());
        friendRequests.add(new HashMap<String, Boolean>());
        friendRequests.add(new HashMap<String, Boolean>());

    }

    private static final DatabaseWrapper instance = new MockDatabaseWrapper();

    public static DatabaseWrapper getInstance() {
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
        for(Object elem : listToQuery){
            list.add(tClass.cast(elem));
        }
        for(MyQuery.WhereClause clause : query.getWhereClauses()){
            list = filterList(list, clause);
        }
        return CompletableFuture.completedFuture(list);
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
                if (field.get(elem).equals(rightOperand)) {
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

    @NotNull
    private Field getField(MyQuery.WhereClause clause, Class clazz) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(clause.getLeftOperand());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Class" + clazz.getName() + " does not contain a " + field.getName() + " field");
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
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                addItemAux(object);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }

    public void modifyItem(final Object object, final int index) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                changeItemAux(object, index);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
    }

    public void removeItem(final Object object, final int index) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                removeItemAux(object, index);
            }
        };
        runOnUiThread(myRunnable);
        synchronized (myRunnable) {
            myRunnable.wait(1000);
        }
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
        int index = Integer.parseInt(documentID);
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                return CompletableFuture.completedFuture((T)users.get(index));
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                return CompletableFuture.completedFuture((T)friendships.get(index));
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                return CompletableFuture.completedFuture((T)friendRequests.get(index));
            case DatabaseWrapper.LOCATIONS_PATH:
                return CompletableFuture.completedFuture((T)locations.get(documentID));
            default:
                return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> getDocument(String collectionName, String documentID) {
        int index = Integer.parseInt(documentID);
        Map<String, Object> result = new HashMap<>();
        switch (collectionName) {
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                result.putAll(friendships.get(index));
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                result.putAll(friendRequests.get(index));
                break;
            default:
                break;
        }
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public <T> CompletableFuture<T> getDocumentWithFieldCondition(String collectionName, String fieldName, String fieldValue, Class<T> type) {
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                for (User u : users) {
                    switch (fieldName) {
                        case "email":
                            if (u.getEmail().equals(fieldValue)) {
                                return CompletableFuture.completedFuture((T)u);
                            }
                    }
                }
            default:
                return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                users.add((User) document);
                friendRequests.add(new HashMap<String, Boolean>());
                friendships.add(new HashMap<String, Boolean>());
                break;
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                friendships.add((Map<String, Boolean>) document);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                friendRequests.add((Map<String, Boolean>) document);
                break;
        }
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        int index = Integer.parseInt(documentID);
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                users.add( Integer.valueOf(((User) document).getUid()), (User) document);
                friendRequests.add(new HashMap<String, Boolean>());
                friendships.add(new HashMap<String, Boolean>());
                break;
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                friendships.get(index).putAll((Map<String, Boolean>) document);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                friendRequests.get(index).putAll((Map<String, Boolean>) document);
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
        int index = Integer.parseInt(documentID);
        switch (collectionName) {
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                friendships.get(index).clear();
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                friendRequests.get(index).remove(new ArrayList<>(updates.keySet()).get(0));
                break;
        }
    }

    @Override
    public void deleteDocument(String collectionName, String documentID) {
        int index = Integer.parseInt(documentID);
        switch (collectionName) {
            case DatabaseWrapper.USERS_PATH:
                users.remove(index);
                break;
            case DatabaseWrapper.FRIENDSHIPS_PATH:
                friendships.remove(index);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS_PATH:
                friendRequests.remove(index);
                break;
        }
    }

    public void setListToQuery(List<Object> listToQuery) {
        this.listToQuery = listToQuery;
    }


    public void resetFriendshipsAndRequests() {
        for (Map m : friendships) {
            m.clear();
        }
        for (Map m : friendRequests) {
            m.clear();
        }
    }

}
