package ch.epfl.balelecbud.notifications.concertFlow;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

public abstract class AbstractConcertFlow extends IntentService {
    public interface FlowCallback extends Serializable {
        void onResult(List<Slot> slots);
    }

    public AbstractConcertFlow(String name) {
        super(name);
    }

    protected abstract void getAllScheduledConcert(FlowCallback callback);

    protected abstract void scheduleNewConcert(Slot newSlot);

    protected abstract void removeConcert(Slot slot);

    protected abstract void removeConcertById(int id);

    /**
     * Handle an Intent that interact with the database
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        String action = intent.getAction();
        if (action.equals(FlowUtil.ACK_CONCERT)) {
            this.removeConcertById(FlowUtil.unpackIdInIntent(intent));
        } else if (action.equals(FlowUtil.SUBSCRIBE_CONCERT)) {
            Slot s = FlowUtil.unpackSlotInIntent(intent);
            if (s != null)
                this.scheduleNewConcert(s);
        } else if (action.equals(FlowUtil.CANCEL_CONCERT)) {
            Slot s = FlowUtil.unpackSlotInIntent(intent);
            if (s != null)
                this.removeConcert(s);
        } else if (action.equals(FlowUtil.GET_ALL_CONCERT)) {
            FlowCallback callback = FlowUtil.unpackCallback(intent);
            this.getAllScheduledConcert(callback);
        }
    }
}
