package ch.epfl.balelecbud.schedule;

import com.google.firebase.database.ChildEventListener;

import java.util.List;
import ch.epfl.balelecbud.schedule.models.Slot;

public class ScheduleProvider {

    static SlotListener subscribeSlots(ScheduleDatabase db, ScheduleAdapterFacade adapter, List<Slot> slots){
        return db.getSlotListener(adapter, slots);
    }

}
