package ch.epfl.balelecbud.util.database;

import android.telecom.Call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.balelecbud.util.Callback;

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
    public <T> void getDocument(final String collectionName, final String documentID, final Class type, final Callback<T> callback) {
        getCollectionReference(collectionName).document(documentID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        callback.onSuccess((T) documentSnapshot.toObject(type));
                    }
                })
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public void updateDocument(String collectionName, String documentID, Map<String, Object> updates, final Callback callback) {
        getCollectionReference(collectionName).document(documentID).update(updates)
                .addOnSuccessListener(getResultlessOnSuccessListener(callback))
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public <T> void storeDocumentWithID(String collectionName, String documentID, final T document, final Callback callback) {
        getCollectionReference(collectionName).document(documentID).set(document, SetOptions.merge())
                .addOnSuccessListener(getResultlessOnSuccessListener(callback))
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public <T> void storeDocument(String collectionName, final T document, final Callback callback) {
        getCollectionReference(collectionName).add(document)
                .addOnSuccessListener(getResultlessOnSuccessListener(callback))
                .addOnFailureListener(getOnFailureListener(callback));
    }

    @Override
    public void deleteDocument(String collectionName, String documentID, final Callback callback) {
        getCollectionReference(collectionName).document(documentID).delete()
                .addOnSuccessListener(getResultlessOnSuccessListener(callback))
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

    private <T> OnSuccessListener getResultlessOnSuccessListener(final Callback callback) {
        return new OnSuccessListener<T>() {
            @Override
            public void onSuccess(T object) {
                callback.onSuccess(null);
            }
        };
    }

    private CollectionReference getCollectionReference(String collectionName) { return FirebaseFirestore.getInstance().collection(collectionName);}
}
