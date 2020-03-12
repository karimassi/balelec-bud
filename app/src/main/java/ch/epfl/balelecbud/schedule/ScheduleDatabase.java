package ch.epfl.balelecbud.schedule;

import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

public interface ScheduleDatabase {

    SlotListener getSlotListener(ScheduleAdapterFacade adapter, List<Slot> slots);

}
