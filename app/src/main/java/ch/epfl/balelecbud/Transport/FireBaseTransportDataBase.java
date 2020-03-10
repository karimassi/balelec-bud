package ch.epfl.balelecbud.Transport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ch.epfl.balelecbud.Transport.Object.Transport;

public class FirebaseTransportDatabase implements TransportDataBase {

    CollectionReference root = FirebaseFirestore.getInstance().collection("transports");

    @Override
    public TransportListener getTransportListener(TransportAdapterFacade adapter, List<Transport> transports) {
        return new TransportListener(adapter, transports, new FirebaseTransportsListener());
    }


    class FirebaseTransportsListener implements g, WrappedListener {
        TransportListener outerListener;

        FirebaseTransportsListener() {
            root.add
            root.addChildEventListener(this);
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            outerListener.transportAdded(dataSnapshot.getValue(Transport.class), dataSnapshot.getKey());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            outerListener.slotChanged(dataSnapshot.getValue(Slot.class), dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            outerListener.slotRemoved(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //nothing for now
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //nothing for now
        }

        @Override
        public void remove() {
            root.removeEventListener(this);
        }

        @Override
        public void registerOuterListener(TransportListener outerListener) {
            if(this.outerListener != null){
                throw new UnsupportedOperationException("outerListener can only be called once");
            }
            this.outerListener = outerListener;
        }
    }
}