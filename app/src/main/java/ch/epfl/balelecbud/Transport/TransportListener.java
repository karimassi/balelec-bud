package ch.epfl.balelecbud.Transport;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.Transport.Object.Transport;

public class TransportListener {
    private static final String TAG = TransportListener.class.getSimpleName();

    private final TransportAdapterFacade adapter;
    private List<Transport> transports;
    private final List<String> transportsIds;
    private WrappedListener inner;

    public TransportListener(TransportAdapterFacade adapter, List<Transport> transports, WrappedListener inner) {
        this.adapter = adapter;
        this.transports = transports;
        this.transportsIds = new LinkedList<>();
        this.inner = inner;
        inner.registerOuterListener(this);
    }

    public void updateTransport(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (queryDocumentSnapshots != null) {
            Log.d(TAG, "updateTransport: transport successfully updated");
            List<Transport> newTransports = new LinkedList<>();
            for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                newTransports.add(d.toObject(Transport.class));
            }
            this.transports = newTransports;
            // TODO update the adapter
        } else {
            Log.w(TAG, "updateTransport: failed to update the transport", e);
        }
    }

    //remove the listener
    public void remove(){
        inner.remove();
    }

    //used for testing for now
    public List<String> getTransportsIds() {
        return Collections.unmodifiableList(transportsIds);
    }

    //TODO add something for cancelled
}
