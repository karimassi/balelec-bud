package ch.epfl.balelecbud.schedule;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ch.epfl.balelecbud.schedule.models.Concert;
import ch.epfl.balelecbud.schedule.models.Slot;

public class FirebaseRealtimeScheduleProvider implements AbstractScheduleProvider {


    private static final String TAG = "FBScheduleProvider";
    DatabaseReference root;

    private static final String SLOT_LOCATION = "slots";

    public FirebaseRealtimeScheduleProvider (String rootPath) {
        root = FirebaseDatabase.getInstance().getReference();//.child(rootPath);
    }


    @Override
    public void subscribeSlots(final RecyclerView.Adapter adapter, final List<Slot> slots, final List<String> slotIds){
        ChildEventListener concertsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                //should log everything at some point
                Slot newSlot = dataSnapshot.getValue(Slot.class);
                String newSlotKey = dataSnapshot.getKey();

                slotIds.add(newSlotKey);
                slots.add(newSlot);
                adapter.notifyItemInserted(slots.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String updatedKey = dataSnapshot.getKey();
                int index = slotIds.indexOf(updatedKey);
                if(index != -1){
                    Slot updatedSlot = dataSnapshot.getValue(Slot.class);
                    slots.set(index, updatedSlot);
                    adapter.notifyItemChanged(index);
                }else{
                    Log.w(TAG, "child " + updatedKey +" changed but was not tracked to start with");
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String removedKey = dataSnapshot.getKey();
                int index = slotIds.indexOf(removedKey);
                if(index != -1){
                    slotIds.remove(index);
                    slots.remove(index);
                    adapter.notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //don't care because we are ordering the list by timeslot anyway
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "subscribeConcerts:onCancelled", databaseError.toException());
            }
        };
        root.child(SLOT_LOCATION).addChildEventListener(concertsListener);
    }
}
