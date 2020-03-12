package ch.epfl.balelecbud.schedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

public class FirebaseScheduleDatabase implements ScheduleDatabase {

    @Override
    public SlotListener getSlotListener(ScheduleAdapterFacade adapter, List<Slot> slots) {
        return new SlotListener(adapter, slots, new FirebaseSlotsListener());
    }


    class FirebaseSlotsListener implements ChildEventListener, WrappedListener {
        SlotListener outerListener;

        FirebaseSlotsListener(){
            FirebaseDatabase.getInstance().getReference().child("slots").addChildEventListener(this);
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            outerListener.slotAdded(dataSnapshot.getValue(Slot.class), dataSnapshot.getKey());
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
            FirebaseDatabase.getInstance().getReference().child("slots").removeEventListener(this);
        }

        @Override
        public void registerOuterListener(SlotListener outerListener) {
            if(this.outerListener != null){
                throw new UnsupportedOperationException("outerListener can only be called once");
            }
            this.outerListener = outerListener;
        }
    }
}
