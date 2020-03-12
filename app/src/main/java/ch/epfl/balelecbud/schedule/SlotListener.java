package ch.epfl.balelecbud.schedule;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

public class SlotListener  {

    private static final String TAG = "SlotListener";

    private final ScheduleAdapterFacade adapter;
    private final List<Slot> slots;
    private final List<String> slotIds;
    private WrappedListener inner;

    public SlotListener(ScheduleAdapterFacade adapter, List<Slot> slots, WrappedListener inner) {
        this.adapter = adapter;
        this.slots = slots;
        this.slotIds = new LinkedList<>();
        this.inner = inner;
        inner.registerOuterListener(this);
    }


    public void slotAdded(Slot newSlot, String newSlotKey) {
        //should log everything at some point
        slotIds.add(newSlotKey);
        slots.add(newSlot);
        adapter.notifyItemInserted(slots.size() - 1);
    }

    public void slotChanged(Slot updatedSlot, String updatedSlotKey) {
        int index = slotIds.indexOf(updatedSlotKey);
        if(index != -1){
            slots.set(index, updatedSlot);
            adapter.notifyItemChanged(index);
        }else{
            Log.w(TAG, "child " + updatedSlotKey +" changed but was not tracked to start with");
        }
    }

    public void slotRemoved(String removedSlotKey) {
        int index = slotIds.indexOf(removedSlotKey);
        if(index != -1){
            slotIds.remove(index);
            slots.remove(index);
            adapter.notifyItemRemoved(index);
        }else{
            Log.w(TAG, "child " + removedSlotKey +" deleted but was not tracked to start with");
        }
    }

    //remove the listener
    public void remove(){
        inner.remove();
    }

    //used for testing for now
    public List<String> getSlotIds() {
        return slotIds;
    }

    //TODO add something for cancelled
}