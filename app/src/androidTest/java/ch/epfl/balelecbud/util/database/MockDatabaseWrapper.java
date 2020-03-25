package ch.epfl.balelecbud.util.database;

import android.app.DownloadManager;

import com.google.rpc.Help;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class MockDatabaseWrapper implements DatabaseWrapper {

    private List<DatabaseListener> listeners;
    private List<Object> listToQuery;

    public MockDatabaseWrapper() {
        listeners = new ArrayList<>();
        listToQuery = new ArrayList<>();
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
        try {
            for (A elem : list) {
                Class clazz = elem.getClass();
                Field field = clazz.getDeclaredField(clause.getLeftOperand());
                // to allow us to modify variable
                field.setAccessible(true);

                // check that the field and our value have the same type,
                // right now they must be exactly the same class
                Object rightOperand = clause.getRightOperand();
                Class classOfRightOperand = rightOperand.getClass();
                Class<?> fieldType = field.getType();
                if (!classOfRightOperand.equals(fieldType)) {
                    //if not of the same class we consider them uncomparable and return the empty list
                    return newList;
                }
                if (clause.getOp() == MyQuery.WhereClause.Operator.EQUAL) {
                    if (field.get(elem).equals(rightOperand)) {
                        newList.add(elem);
                    }
                } else if (field.get(elem) instanceof Comparable) {
                    Comparable rightOperandComparable = (Comparable) field.get(elem);
                    // fine because we checked before that the types were equal, would be nice
                    // to find a way so that the IDE doesn't complain
                    int result = rightOperandComparable.compareTo(rightOperand);
                    if (evaluateResult(clause.getOp(), result)) {
                        newList.add(elem);
                    }
                }
            }
        } catch(Exception e){
            return newList;
        }
        return newList;
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

    public void setListToQuery(List<Object> listToQuery) {
        this.listToQuery = listToQuery;
    }

}
