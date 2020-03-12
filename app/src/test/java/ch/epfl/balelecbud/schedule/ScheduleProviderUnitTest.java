package ch.epfl.balelecbud.schedule;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

import static org.junit.Assert.*;

public class ScheduleProviderUnitTest {

    ScheduleDatabase mock = new ScheduleDatabase() {
        @Override
        public SlotListener getSlotListener(ScheduleAdapterFacade adapter, List<Slot> slots) {
            return new SlotListener(adapter, slots, new WrappedListener() {
                @Override
                public void remove() {

                }

                @Override
                public void registerOuterListener(SlotListener outerListener) {

                }
            });
        }
    };

    @Test
    public void onChildAddedCallsNotifyItemInsertedAndUpdateLists() throws InterruptedException {

        final LinkedList<Slot> slots = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");

        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                assertEquals(0, position);
                assertEquals(slot.getArtistName(), slots.get(0).getArtistName());
                assertEquals(slot.getTimeSlot(), slots.get(0).getTimeSlot());
                assertEquals(slot.getSceneName(), slots.get(0).getSceneName());
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

        SlotListener listener = mock.getSlotListener(adapterFacade, slots);

        listener.slotAdded(slot, "slot1");

    }

    @Test
    public void onChildChangedCallsNotifyItemChangedAndUpdateList() throws InterruptedException {

        final String newTimeSlot = "newTimeSlot";
        final LinkedList<Slot> slots = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        final Slot updatedSlot = new Slot("TestNamel", newTimeSlot, "TestScene");
        final String slotKey = "slot1";

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
            }

            @Override
            public void notifyItemRemoved(int position) {
            }
        };

        SlotListener listener = mock.getSlotListener(adapterFacade, slots);

        listener.slotAdded(slot, slotKey);
        listener.slotChanged(updatedSlot, slotKey);

    }

    @Test
    public void onChildRemovedCallsNotifyItemRemoveAndUpdateLists() throws InterruptedException {

        final LinkedList<Slot> slots = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        final String slotKey = "slot1";
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
            }
        };

        SlotListener listener = mock.getSlotListener(adapterFacade, slots);

        listener.slotAdded(slot, slotKey);
        listener.slotRemoved(slotKey);
    }

    @Test
    public void onChildChangedDoesNotCallNotifyIfItemNotInList() throws InterruptedException {

        final String newTimeSlot = "newTimeSlot";
        final LinkedList<Slot> slots = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        final String slotKey1 = "slot1";
        final Slot slot2 = new Slot("TestName2", "TestTimeslot2", "TestScene2");
        final String slotKey2 = "slot2";

        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            int counterChanged = 0;
            @Override
            public void notifyItemInserted(int position) {

            }

            @Override
            public void notifyItemChanged(int position) {
                //check that the only notified item changed is the first one
                counterChanged += 1;
                assertEquals(1, counterChanged);
                assertEquals(0, position);
            }

            @Override
            public void notifyItemRemoved(int position) {

            }
        };

        SlotListener listener = mock.getSlotListener(adapterFacade, slots);

        listener.slotAdded(slot, slotKey1);
        listener.slotAdded(slot2, slotKey2);

        listener.getSlotIds().remove(1);

        listener.slotChanged(slot, slotKey1);
        listener.slotChanged(slot2, slotKey2);

    }

    @Test
    public void onChildRemovedDoesNotCallNotifyIfItemNotInList() throws InterruptedException {

        final String newTimeSlot = "newTimeSlot";
        final LinkedList<Slot> slots = new LinkedList<>();
        final Slot slot = new Slot("TestNamel", "TestTimeslot", "TestScene");
        final String slotKey1 = "slot1";
        final Slot slot2 = new Slot("TestName2", "TestTimeslot2", "TestScene2");
        final String slotKey2 = "slot2";

        ScheduleAdapterFacade adapterFacade = new ScheduleAdapterFacade() {
            int counterRemoved = 0;
            @Override
            public void notifyItemInserted(int position) {

            }

            @Override
            public void notifyItemChanged(int position) {

            }

            @Override
            public void notifyItemRemoved(int position) {
                //check that the only notified item removed is the first one
                counterRemoved += 1;
                assertEquals(1, counterRemoved);
                assertEquals(0, position);
            }
        };

        SlotListener listener = mock.getSlotListener(adapterFacade, slots);

        listener.slotAdded(slot, slotKey1);
        listener.slotAdded(slot2, slotKey2);

        listener.getSlotIds().remove(1);

        listener.slotRemoved(slotKey2);
        listener.slotRemoved(slotKey1);
    }
}
