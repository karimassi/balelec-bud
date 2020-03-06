package ch.epfl.balelecbud;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.LinkedList;

import ch.epfl.balelecbud.schedule.AbstractScheduleProvider;
import ch.epfl.balelecbud.schedule.FirebaseRealtimeScheduleProvider;
import ch.epfl.balelecbud.schedule.ScheduleActivity;
import ch.epfl.balelecbud.schedule.ScheduleAdapterFacade;
import ch.epfl.balelecbud.schedule.models.Slot;

import static org.junit.Assert.*;

public class FirebaseRealtimeScheduleProviderTest {

    @Rule
    public final ActivityTestRule<ScheduleActivity> mScheduleActivity =
            new ActivityTestRule<>(ScheduleActivity.class);

    @Before
    public void setup(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("test");
        ref.child("slots").setValue(null);
    }

    @Test
    public void onChildAddedCallsNotifyItemInsertedAndUpdateLists() throws InterruptedException {

        final Object syncObject = new Object();
        String rootPath = "test";

        AbstractScheduleProvider provider = new FirebaseRealtimeScheduleProvider(rootPath);
        final LinkedList<Slot> slots = new LinkedList<>();
        LinkedList<String> slotIds = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                assertEquals(0, position);
                assertEquals(slot.getArtistName(), slots.get(0).getArtistName());
                assertEquals(slot.getTimeSlot(), slots.get(0).getTimeSlot());
                assertEquals(slot.getSceneName(), slots.get(0).getSceneName());

                synchronized (syncObject){
                    syncObject.notify();
                }
            }

            @Override
            public void notifyItemChanged(int position) {
                //empty by design
            }

            @Override
            public void notifyItemRemoved(int position) {
                //empty by design
            }
        };

        ChildEventListener listener = provider.subscribeSlots(adapterFacade, slots, slotIds);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(rootPath);
        ref.child("slots").child("slot2").setValue(slot);

        synchronized (syncObject){
            syncObject.wait();
        }
        ref.child("slots").setValue(null);
        ref.child("slots").removeEventListener(listener);

    }

    @Test
    public void onChildChangedCallsNotifyItemChangedAndUpdateList() throws InterruptedException {

        final Object syncObject = new Object();
        String rootPath = "test";
        final String newTimeSlot = "newTimeSlot";
        AbstractScheduleProvider provider = new FirebaseRealtimeScheduleProvider(rootPath);
        final LinkedList<Slot> slots = new LinkedList<>();
        LinkedList<String> slotIds = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {

            }

            @Override
            public void notifyItemChanged(int position) {
                assertEquals(0, position);
                //assert artistName and sceneName did not change
                assertEquals(slot.getArtistName(), slots.get(position).getArtistName());
                assertEquals(slot.getSceneName(), slots.get(position).getSceneName());
                assertEquals(newTimeSlot, slots.get(position).getTimeSlot());

                synchronized (syncObject){
                    syncObject.notify();
                }
            }

            @Override
            public void notifyItemRemoved(int position) {
                //empty by design
            }
        };

        ChildEventListener listener = provider.subscribeSlots(adapterFacade, slots, slotIds);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(rootPath);
        ref.child("slots").child("slot2").setValue(slot);
        ref.child("slots").child("slot2").child("timeSlot").setValue(newTimeSlot);
        ref.child("slots").setValue(null);

        synchronized (syncObject){
            syncObject.wait();
        }

        ref.child("slots").removeEventListener(listener);
    }

    @Test
    public void onChildRemovedCallsNotifyItemRemoveAndUpdateLists() throws InterruptedException {

        final Object syncObject = new Object();
        String rootPath = "test";
        final String newTimeSlot = "newTimeSlot";
        AbstractScheduleProvider provider = new FirebaseRealtimeScheduleProvider(rootPath);
        final LinkedList<Slot> slots = new LinkedList<>();
        final LinkedList<String> slotIds = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {

            }

            @Override
            public void notifyItemChanged(int position) {

            }

            @Override
            public void notifyItemRemoved(int position) {
                assertEquals(0, position);
                assertEquals(0, slots.size());
                assertEquals(0, slotIds.size());

                synchronized (syncObject){
                    syncObject.notify();
                }
            }
        };

        ChildEventListener listener = provider.subscribeSlots(adapterFacade, slots, slotIds);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(rootPath);
        ref.child("slots").child("slot2").setValue(slot);
        ref.child("slots").setValue(null);

        synchronized (syncObject){
            syncObject.wait();
        }

        ref.child("slots").removeEventListener(listener);
    }

    @Test
    public void onChildRemovedDoesNotCallNotifyIfItemNotInList() throws InterruptedException {

        final Object syncObject = new Object();
        String rootPath = "test";
        final String newTimeSlot = "newTimeSlot";
        AbstractScheduleProvider provider = new FirebaseRealtimeScheduleProvider(rootPath);
        final LinkedList<Slot> slots = new LinkedList<>();
        final LinkedList<String> slotIds = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        final Slot slot2 = new Slot("TestName2", "TestTimeslot2", "TestScene2");
        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            int counterRemoved = 0;
            @Override
            public void notifyItemInserted(int position) {
                int indexOfSlot2 = slotIds.indexOf("slot2");
                if(indexOfSlot2 != -1){
                    slots.remove(indexOfSlot2);
                    slotIds.remove(indexOfSlot2);
                }
            }

            @Override
            public void notifyItemChanged(int position) {

            }

            @Override
            public void notifyItemRemoved(int position) {
                //check that the only notified item to be removed is the first one
                counterRemoved += 1;
                assertEquals(1, counterRemoved);
                assertEquals(0, position);

                synchronized (syncObject){
                    syncObject.notify();
                }
            }
        };

        ChildEventListener listener = provider.subscribeSlots(adapterFacade, slots, slotIds);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(rootPath);
        ref.child("slots").child("slot1").setValue(slot);
        ref.child("slots").child("slot2").setValue(slot2);

        ref.child("slots").child("slot2").setValue(null);
        ref.child("slots").child("slot1").setValue(null);

        synchronized (syncObject){
            syncObject.wait();
        }

        ref.child("slots").removeEventListener(listener);

    }

    @Test
    public void onChildChangedDoesNotCallNotifyIfItemNotInList() throws InterruptedException {

        final Object syncObject = new Object();
        String rootPath = "test";
        final String newTimeSlot = "newTimeSlot";
        AbstractScheduleProvider provider = new FirebaseRealtimeScheduleProvider(rootPath);
        final LinkedList<Slot> slots = new LinkedList<>();
        final LinkedList<String> slotIds = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        final Slot slot2 = new Slot("TestName2", "TestTimeslot2", "TestScene2");
        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            int counterChanged = 0;
            @Override
            public void notifyItemInserted(int position) {
                int indexOfSlot2 = slotIds.indexOf("slot2");
                if(indexOfSlot2 != -1){
                    slots.remove(indexOfSlot2);
                    slotIds.remove(indexOfSlot2);
                }
            }

            @Override
            public void notifyItemChanged(int position) {
                //check that the only notified item changed is the first one
                counterChanged += 1;
                assertEquals(1, counterChanged);
                assertEquals(0, position);

                synchronized (syncObject){
                    syncObject.notify();
                }
            }

            @Override
            public void notifyItemRemoved(int position) {

            }
        };

        ChildEventListener listener = provider.subscribeSlots(adapterFacade, slots, slotIds);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(rootPath);
        ref.child("slots").child("slot1").setValue(slot);
        ref.child("slots").child("slot2").setValue(slot2);

        ref.child("slots").child("slot2").child("timeSlot").setValue(null);
        ref.child("slots").child("slot1").child("timeSlot").setValue(null);

        synchronized (syncObject){
            syncObject.wait();
        }

        ref.child("slots").setValue(null);
        ref.child("slots").removeEventListener(listener);

    }
}
