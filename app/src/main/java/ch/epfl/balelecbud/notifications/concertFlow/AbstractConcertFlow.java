package ch.epfl.balelecbud.notifications.concertFlow;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import ch.epfl.balelecbud.schedule.models.Slot;

public abstract class AbstractConcertFlow extends IntentService {
    public AbstractConcertFlow(String name) {
        super(name);
    }

    protected abstract void getAllScheduledConcert(Intent callbackIntent);

    protected abstract void scheduleNewConcert(Slot newSlot);

    protected abstract void removeConcert(Slot slot);

    protected abstract void removeConcertById(int id);

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        String action = intent.getAction();
        Log.d("ConcertFlow", "onHandleIntent: action = " + action);
        switch (action) {
            case FlowUtil.ACK_CONCERT:
                this.removeConcertById(FlowUtil.unpackIdInIntent(intent));
                break;
            case FlowUtil.SUBSCRIBE_CONCERT: {
                Slot s = FlowUtil.unpackSlotInIntent(intent);
                if (s != null)
                    this.scheduleNewConcert(s);
                break;
            }
            case FlowUtil.CANCEL_CONCERT: {
                Slot s = FlowUtil.unpackSlotInIntent(intent);
                if (s != null)
                    this.removeConcert(s);
                break;
            }
            case FlowUtil.GET_ALL_CONCERT:
                Intent callbackIntent = FlowUtil.unpackIntentInIntent(intent);
                if (callbackIntent != null)
                    this.getAllScheduledConcert(callbackIntent);
                break;
        }
    }
}
