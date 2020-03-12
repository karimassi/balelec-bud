package ch.epfl.balelecbud.festivalInformation;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class FestivalInformationDatabase implements BasicDatabase {

    private FestivalInformationListener listener;
    private ListenerRegistration lr;

    @Override
    public void listen() {
        lr = FirebaseFirestore.getInstance().collection("festivalInfo").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!= null | listener == null) return;
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            listener.addInformation(dc.getDocument().toObject(FestivalInformation.class));
                            break;
                        case MODIFIED:
                            listener.modifyInformation(dc.getDocument().toObject(FestivalInformation.class), dc.getOldIndex());
                            break;
                        case REMOVED:
                            listener.removeInformation(dc.getDocument().toObject(FestivalInformation.class), dc.getOldIndex());
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void registerListener(FestivalInformationListener listener) {
        this.listener = listener;
    }

    @Override
    public void unregisterListener() {
        if (lr != null)  lr.remove();
    }
}
