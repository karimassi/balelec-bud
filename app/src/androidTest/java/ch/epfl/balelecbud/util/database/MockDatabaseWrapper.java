package ch.epfl.balelecbud.util.database;

import java.util.ArrayList;
import java.util.List;

public class MockDatabaseWrapper implements DatabaseWrapper {

    private List<DatabaseListener> listeners;

    public MockDatabaseWrapper() {
        listeners = new ArrayList<>();
    }


    @Override
    public void unregisterListener(DatabaseListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void listen(String collectionName, DatabaseListener listener) {
        listeners.add(listener);
    }

    public void addItem(Object item) {
        for (DatabaseListener l : listeners) {
            l.onItemAdded(item);
        }
    }

    public void changeItem(Object item, int position) {
        for (DatabaseListener l : listeners) {
            l.onItemChanged(item, position);
        }
    }

    public void removeItem(Object item, int position) {
        for (DatabaseListener l : listeners) {
            l.onItemRemoved(item, position);
        }
    }
}
