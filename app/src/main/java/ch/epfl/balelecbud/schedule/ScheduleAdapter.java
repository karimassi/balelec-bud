package ch.epfl.balelecbud.schedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.balelecbud.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ScheduleAdapter extends RecyclerView.Adapter {

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private List<Slot> slots;
    private List<String> slotids = new ArrayList<>();

    public ScheduleAdapter(final List<Slot> slots, DatabaseReference ref) {
        this.slots = slots;

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                Slot slot = dataSnapshot.getValue(Slot.class);

                slotids.add(dataSnapshot.getKey());
                slots.add(slot);
                notifyItemInserted(slots.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                Slot newSlot = dataSnapshot.getValue(Slot.class);
                String slotKey = dataSnapshot.getKey();

                int slotIndex = slotids.indexOf(slotKey);
                if (slotIndex > -1) {
                    slots.set(slotIndex, newSlot);
                    notifyItemChanged(slotIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + slotKey);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                String slotKey = dataSnapshot.getKey();

                int slotIndex = slotids.indexOf(slotKey);
                if (slotIndex > -1) {
                    slotids.remove(slotIndex);
                    slots.remove(slotIndex);
                    notifyItemRemoved(slotIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + slotKey);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                Slot movedSlot = dataSnapshot.getValue(Slot.class);
                String slotKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };
        ref.addChildEventListener(childEventListener);
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View scheduleView = inflater.inflate(R.layout.item_schedule, parent, false);
        ScheduleViewHolder viewHolder = new ScheduleViewHolder(scheduleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // ???????????????
    }

    public void onBindViewHolder(ScheduleViewHolder viewHolder, int position) {
        Slot slot = slots.get(position);
        viewHolder.timeSlotView.setText(slot.getTimeSlot());
        viewHolder.artistNameView.setText(slot.getArtistName());
        viewHolder.sceneNameView.setText(slot.getSceneName());
    }

    public int getItemCount() {
        return slots.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }
}
