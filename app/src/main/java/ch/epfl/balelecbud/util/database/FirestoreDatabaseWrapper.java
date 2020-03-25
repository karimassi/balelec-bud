package ch.epfl.balelecbud.util.database;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FirestoreDatabaseWrapper implements DatabaseWrapper {

    private Map<DatabaseListener, ListenerRegistration> registrationMap;

    private static final DatabaseWrapper instance = new FirestoreDatabaseWrapper();

    private FirestoreDatabaseWrapper() {
        registrationMap = new HashMap<>();
    }

    @Override
    public void unregisterListener(DatabaseListener listener) {
        registrationMap.remove(listener);
    }

    @Override
    public void listen(String collectionName, final DatabaseListener listener) {
        ListenerRegistration lr = FirebaseFirestore.getInstance().collection(collectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
}
