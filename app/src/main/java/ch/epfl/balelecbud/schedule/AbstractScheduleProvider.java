package ch.epfl.balelecbud.schedule;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ch.epfl.balelecbud.schedule.models.Slot;

public interface AbstractScheduleProvider {

    void subscribeSlots(RecyclerView.Adapter adapter, List<Slot> slots, List<String> concertIds);

}
