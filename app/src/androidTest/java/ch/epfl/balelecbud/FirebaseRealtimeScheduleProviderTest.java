package ch.epfl.balelecbud;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
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

    @After
    public void teardown(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("test");
        ref.child("slots").child("slot2").setValue(null);
    }

    @Test
    public void onChildAddedWorksAsExpected() throws InterruptedException {

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

        provider.subscribeSlots(adapterFacade, slots, slotIds);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(rootPath);
        ref.child("slots").child("slot2").setValue(slot);

        synchronized (syncObject){
            syncObject.wait();
        }
    }
}
