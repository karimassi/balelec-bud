package ch.epfl.balelecbud.util.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.util.Callback;

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
    public <T> void getDocument(final String collectionName, final String documentID, final Class type, final Callback<T> callback) {
        FirebaseFirestore.getInstance().collection(collectionName).document(documentID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        callback.onSuccess((T) documentSnapshot.toObject(type));
                    }
                })
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public <T> void storeDocumentWithID(String collectionName, String documentID, final T document, final Callback<T> callback) {

        FirebaseFirestore.getInstance().collection(collectionName).document(documentID).set(document)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(document);
                    }
                })
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public <T> void storeDocument(String collectionName, final T document, final Callback<T> callback) {

        FirebaseFirestore.getInstance().collection(collectionName).add(document)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callback.onSuccess(document);
                    }
                })
                .addOnFailureListener(getOnFailureListener(callback));
    }

    public static DatabaseWrapper getInstance() {
        return instance;
    }

    private OnFailureListener getOnFailureListener(final Callback callback) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getLocalizedMessage());
            }
        };
    }
}
