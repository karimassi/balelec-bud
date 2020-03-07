package ch.epfl.balelecbud.schedule;

import com.google.firebase.database.ChildEventListener;

import java.util.List;
import ch.epfl.balelecbud.schedule.models.Slot;

public interface AbstractScheduleProvider {

    //returns listener to be able to remove it at any point
    ChildEventListener subscribeSlots(ScheduleAdapterFacade adapter, List<Slot> slots, List<String> slotIds);

}
