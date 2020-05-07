package ch.epfl.balelecbud.util.database;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.util.TaskToCompletableFutureAdapter;

public class FirestoreDatabase implements Database {
    private static final String TAG = FirestoreDatabase.class.getSimpleName();
    private static final FirestoreDatabase instance = new FirestoreDatabase();
    private final Map<String, ListenerRegistration> registrations = new HashMap<>();

    public static FirestoreDatabase getInstance() {
        return instance;
    }

    private FirestoreDatabase() {}


    @Override
    public void unregisterDocumentListener(String collectionName, String documentID) {
        ListenerRegistration lr = registrations.remove(collectionName + "/" + documentID);
        if (lr != null)
            lr.remove();
    }

    @Override
    public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) {
        Log.d(TAG, "listenDocument() called with: collectionName = [" + collectionName + "], documentID = [" + documentID + "], type = [" + type + "]");
        ListenerRegistration lr = getDocumentReference(collectionName + "/" + documentID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    Log.d(TAG, "eventListener called on: collectionName = [" + collectionName + "], documentID = [" + documentID + "]" +
                            ", with document = [" + documentSnapshot + "], e = [" + e + "]" );
                    if (documentSnapshot == null) {
                        Log.w(TAG, "listenDocument: failed to add a SnapshotListener on file : " + documentID +
                                " in collection : " + collectionName, e);
                    } else {
                        consumer.accept(documentSnapshot.toObject(type));
                    }
                });
        registrations.put(collectionName + "/" + documentID, lr);
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
        getCollectionReference(collectionName).document(documentID).update(updates);
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName,
                                                           String documentID, final T document) {
        return new TaskToCompletableFutureAdapter<>(getCollectionReference(collectionName)
                .document(documentID).set(document, SetOptions.merge()));
    }

    @Override
    public <T> void storeDocument(String collectionName, final T document) {
        getCollectionReference(collectionName).add(document);
    }

    @Override
    public void deleteDocumentWithID(String collectionName, String documentID) {
        getCollectionReference(collectionName).document(documentID).delete();
    }

    @Override
    public <T> CompletableFuture<List<T>> query(MyQuery query, final Class<T> tClass) {
        CompletableFuture<QuerySnapshot> future =
                new TaskToCompletableFutureAdapter<>(FirestoreQueryConverter.convert(query).get());
        return future.thenApply(value -> value.toObjects(tClass));
    }

    public CompletableFuture<List<Map<String, Object>>> query(MyQuery query) {
        CompletableFuture<QuerySnapshot> future =
                new TaskToCompletableFutureAdapter<>(FirestoreQueryConverter.convert(query).get());
        return future.thenApply( value -> {
            List<Map<String, Object>> result = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot: value) {
                result.add(documentSnapshot.getData());
            }
            return result;
        });
    }

    private CollectionReference getCollectionReference(String collectionName) {
        return FirebaseFirestore.getInstance().collection(collectionName);
    }

    private DocumentReference getDocumentReference(String documentPath) {
        return FirebaseFirestore.getInstance().document(documentPath);
    }

}
