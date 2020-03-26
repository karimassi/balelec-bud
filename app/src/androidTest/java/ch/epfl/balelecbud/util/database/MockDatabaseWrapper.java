package ch.epfl.balelecbud.util.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.FriendRequest;
import ch.epfl.balelecbud.models.User;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class MockDatabaseWrapper implements DatabaseWrapper {

    private List<DatabaseListener> listeners;

    private Map<String, Object> db = new HashMap<String, Object>() {
        {
            put("users", new ArrayList<User>());
            put("friendships", new ArrayList<Map<String, Boolean>>());
            put("friendRequests", new ArrayList<FriendRequest>());
        }
    };

    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Map<String, Boolean>> friendships = new ArrayList<>();
    private ArrayList<FriendRequest> friendRequests = new ArrayList<>();

    private MockDatabaseWrapper() {
        listeners = new ArrayList<>();
        users.add(new User("karim@epfl.ch", "karim@epfl.ch", "0"));
        users.add(new User("celine@epfl.ch", "celine@epfl.ch", "1"));
        friendships.add(new HashMap<String, Boolean>());
        friendships.add(new HashMap<String, Boolean>());
        friendRequests.add(null);
        friendRequests.add(null);

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
    public <T> CompletableFuture<T> getDocument(String collectionName, String documentID, Class<T> type) {
        int index = Integer.parseInt(documentID);
        switch (collectionName) {
            case DatabaseWrapper.USERS:
                return CompletableFuture.completedFuture((T)users.get(index));
            case DatabaseWrapper.FRIENDSHIPS:
                return CompletableFuture.completedFuture((T)friendships.get(index));
            case DatabaseWrapper.FRIEND_REQUESTS:
                return CompletableFuture.completedFuture((T)friendRequests.get(index));
            default:
                return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public <T> void storeDocument(String collectionName, T document) {
        switch (collectionName) {
            case DatabaseWrapper.USERS:
                users.add((User) document);
                Log.d("HERE", users.toString());
                break;
            case DatabaseWrapper.FRIENDSHIPS:
                friendships.add((Map<String, Boolean>) document);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS:
                friendRequests.add((FriendRequest) document);
                break;
        }
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
        int index = Integer.parseInt(documentID);
        switch (collectionName) {
            case DatabaseWrapper.FRIENDSHIPS:
                friendships.get(index).putAll((Map<String, Boolean>) document);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS:
                friendRequests.add(index, (FriendRequest) document);
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
            case DatabaseWrapper.FRIENDSHIPS:
                friendships.get(index).clear();
                break;
        }
    }

    @Override
    public void deleteDocument(String collectionName, String documentID) {
        int index = Integer.parseInt(documentID);
        switch (collectionName) {
            case DatabaseWrapper.USERS:
                users.remove(index);
                break;
            case DatabaseWrapper.FRIENDSHIPS:
                friendships.remove(index);
                break;
            case DatabaseWrapper.FRIEND_REQUESTS:
                friendRequests.remove(index);
                break;
        }
    }
}
