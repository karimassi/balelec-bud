package ch.epfl.balelecbud.util.database;

import android.telecom.Call;

import androidx.test.espresso.intent.Intents;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class MockDatabaseWrapper implements DatabaseWrapper {

    private List<DatabaseListener> listeners;

    private Map<String, Object> db = new HashMap<String, Object>() {
        {
            put("users", new ArrayList<User>());
            put("friendships", new ArrayList<ArrayList<String>>());
            put("friendRequests", new ArrayList<User>());
        }
    };

    private MockDatabaseWrapper() {
        listeners = new ArrayList<>();
        ((ArrayList<User>) db.get("users")).add(new User("karim@epfl.ch", null, "karim@epfl.ch", "0"));
        ((ArrayList<User>) db.get("users")).add(new User("celine@epfl.ch", null, "celine@epfl.ch", "0"));
        ((ArrayList<ArrayList<String>>) db.get("friendships")).add(new ArrayList<String>());
        ((ArrayList<ArrayList<String>>) db.get("friendships")).add(new ArrayList<String>());
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
    public <T> void getDocument(String collectionName, String documentID, Class type, Callback<T> callback) {
        int index = Integer.parseInt(documentID);
        if (((ArrayList<T>) db.get(collectionName)).size() < index) {
            callback.onFailure("No such item in DB");
        }
        else {
            callback.onSuccess(((ArrayList<T>) db.get(collectionName)).get(index) );
        }
    }

    @Override
    public <T> void storeDocument(String collectionName, T document, Callback<T> callback) {
        ((ArrayList<T>) db.get(collectionName)).add(document);
        callback.onSuccess(document);
    }

    @Override
    public <T> void storeDocumentWithID(String collectionName, String documentID, T document, Callback<T> callback) {
        storeDocument(collectionName, document, callback);
    }

    @Override
    public <T> void updateArrayElements(String collectionName, String documentID, String arrayName, T document) {
        ((ArrayList<ArrayList<T>>) db.get(collectionName)).get(Integer.parseInt(documentID)).add(document);
    }
}
