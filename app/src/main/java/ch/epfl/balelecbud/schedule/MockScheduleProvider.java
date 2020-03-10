package ch.epfl.balelecbud.schedule;

import com.google.firebase.database.ChildEventListener;

import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

public class MockScheduleProvider {
    public ChildEventListener subscribeSlots(ScheduleAdapterFacade adapter, List<Slot> slots, List<String> slotIds) {
        Slot slot1 = new Slot("Mr Oizo", "19h - 20h", "Grande scène") ;
        Slot slot2 = new Slot("Walking Furret", "20h - 21h", "Les Azimutes") ;
        Slot slot3 = new Slot("There's no need to be upset", "19h - 20h", "Scène Sat'") ;

        String id1 = "oui";
        String id2 = "ouioui";
        String id3 = "ouiouioui";

        slots.add(slot1);
        slots.add(slot2);
        slots.add(slot3);

        return null;
    }
}
