package ch.epfl.balelecbud.util.database;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import ch.epfl.balelecbud.util.TaskToCompletableFutureAdapter;

public class FirestoreDatabaseWrapper implements DatabaseWrapper {

    private Map<DatabaseListener, ListenerRegistration> registrationMap;

    private static final DatabaseWrapper instance = new FirestoreDatabaseWrapper();

    private FirestoreDatabaseWrapper() {
        registrationMap = new HashMap<>();
    }

    @Override
    public void unregisterListener(DatabaseListener listener) {
        Objects.requireNonNull(registrationMap.remove(listener)).remove();
    }

    @Override
    public void listen(String collectionName, final DatabaseListener listener) {
        ListenerRegistration lr = getCollectionReference(collectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null | listener == null) return;
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            listener.onItemAdded(dc.getDocument().toObject(listener.getType()));
                            break;
                        case MODIFIED:
                            listener.onItemChanged(dc.getDocument().toObject(listener.getType()), dc.getOldIndex());
                            break;
                        case REMOVED:
                            listener.onItemRemoved(dc.getDocument().toObject(listener.getType()), dc.getOldIndex());
                            break;
                    }
                }
            }
        });
        registrationMap.put(listener, lr);
    }

    @Override
    public <T> CompletableFuture<T> getDocument(final String collectionName, final String documentID, final Class<T> type) {
        CompletableFuture<DocumentSnapshot> result = new TaskToCompletableFutureAdapter<>(getCollectionReference(collectionName).document(documentID).get());
        return result.thenApply(new Function<DocumentSnapshot, T>() {
            @Override
            public T apply(DocumentSnapshot documentSnapshot) {
                return documentSnapshot.toObject(type);
            }
        });
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
        getCollectionReference(collectionName).document(documentID).update(updates);
    }

    @Override
    public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, final T document) {
        return new TaskToCompletableFutureAdapter<>(getCollectionReference(collectionName).document(documentID).set(document, SetOptions.merge()));
    }

    @Override
    public <T> void storeDocument(String collectionName, final T document) {
        getCollectionReference(collectionName).add(document);
    }

    @Override
    public void deleteDocument(String collectionName, String documentID) {
        getCollectionReference(collectionName).document(documentID).delete();
    }

    @Override
    public <T> CompletableFuture<List<T>> query(MyQuery query, final Class<T> tClass){
        final CompletableFuture<List<T>> compFuture =  new CompletableFuture<>();
        FirestoreQueryConverter.convert(query).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                compFuture.complete(queryDocumentSnapshots.toObjects(tClass));
            }
        });
        return compFuture;
    }

    public static DatabaseWrapper getInstance() {
        return instance;
    }

    private CollectionReference getCollectionReference(String collectionName) { return FirebaseFirestore.getInstance().collection(collectionName);}
}
