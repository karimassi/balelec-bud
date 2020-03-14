package ch.epfl.balelecbud.transport;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import ch.epfl.balelecbud.transport.objects.Transport;

public class FirebaseTransportDatabase implements TransportDatabase {
    private CollectionReference root = FirebaseFirestore.getInstance().collection("transports");

    @Override
    public TransportListener getTransportListener(TransportAdapterFacade adapter, List<Transport> transports) {
        return new TransportListener(adapter, transports, new FirebaseTransportsListener());
    }

    class FirebaseTransportsListener implements EventListener<QuerySnapshot>, WrappedListener {
        private final String TAG = FirebaseTransportsListener.class.getSimpleName();
        private TransportListener outerListener;
        private ListenerRegistration lr;

        FirebaseTransportsListener() {
            this.lr = root.addSnapshotListener(this);
        }

        @Override
        public void remove() {
            this.lr.remove();
        }

        @Override
        public void registerOuterListener(TransportListener outerListener) {
            if(this.outerListener != null){
                throw new UnsupportedOperationException("outerListener can only be called once");
            }
            this.outerListener = outerListener;
        }

        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            if (queryDocumentSnapshots != null) {
                Log.d(TAG, "updateTransport: transport successfully updated");
                for (DocumentChange d : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (d.getType()) {
                        case ADDED:
                            this.outerListener.addTransport(d.getDocument().toObject(Transport.class), d.getNewIndex());
                            break;
                        case REMOVED:
                            this.outerListener.removeTransport(d.getOldIndex());
                            break;
                        case MODIFIED:
                            this.outerListener.modifyTransport(d.getDocument().toObject(Transport.class), d.getOldIndex());
                    }
                }
            } else {
                Log.w(TAG, "updateTransport: failed to update the transport", e);
            }
        }
    }
}