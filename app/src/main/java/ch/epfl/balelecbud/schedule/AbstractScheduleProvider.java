package ch.epfl.balelecbud.schedule;

import java.util.List;
import ch.epfl.balelecbud.schedule.models.Slot;

public interface AbstractScheduleProvider {

    void subscribeSlots(ScheduleAdapterFacade adapter, List<Slot> slots, List<String> slotIds);

}
