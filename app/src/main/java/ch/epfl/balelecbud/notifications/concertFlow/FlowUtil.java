package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

import ch.epfl.balelecbud.schedule.models.Slot;

public final class FlowUtil {
    private static final String TAG = "ch.epfl.balelecbud.notifications.concertFlow";
    public static final String CANCEL_CONCERT = TAG + ".CANCEL_CONCERT";
    public static final String SUBSCRIBE_CONCERT =  TAG + ".SUBSCRIBE_CONCERT";
    public static final String ACK_CONCERT = TAG + ".ACK_CONCERT";
    public static final String GET_ALL_CONCERT = TAG + ".GET_ALL_CONCERT";
    public static final String ID = TAG + ".ID";
    public static final String SLOT = TAG + ".SLOT";
    public static final String CALLBACK = TAG + ".CALLBACK";

    /**
     * Unpack the given Intent to retrieve the Id stored in it
     *
     * @param intent the Intent to unpack
     * @return the Id stored in the Intent or @code{-1} if the Intent wasn't properly formatted
     */
    public static int unpackIdInIntent(Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACK_CONCERT)) {
            return intent.getIntExtra(ID, -1);
        } else {
            return -1;
        }
    }

    /**
     * Pack the given Id into an Intent formatted for @code{unpackIdInIntent}
     * @param context the context from which the Intent is created
     * @param id      the id to pack in the Intent
     * @return        the id packed in an Intent
     */
    public static Intent packAckIntentWithId(Context context, int id) {
        Intent intent = new Intent(context, ConcertFlow.class);
        intent.setAction(ACK_CONCERT);
        Bundle b = new Bundle();
        b.putInt(ID, id);
        intent.putExtras(b);
        return intent;
    }

    /**
     * Unpack the given Intent to retrieve the Slot stored in it
     *
     * @param intent the Intent to unpack
     * @return the Slot stored in the Intent or @code{null} if the Intent wasn't properly formatted
     */
    public static Slot unpackSlotInIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null &&
                    (action.equals(SUBSCRIBE_CONCERT) || action.equals(CANCEL_CONCERT))) {
                Serializable s = intent.getSerializableExtra(SLOT);
                if (s instanceof Slot)
                    return (Slot) s;
            }
        }
        return null;
    }

    /**
     * Pack the given Slot into an Intent formatted for @code{unpackSlotInIntent} to cancel a concert
     * @param context the context from which the Intent is created
     * @param slot    the slot to pack in the Intent
     * @return        the slot packed in an Intent
     */
    public static Intent packCancelIntentWithSlot(Context context, Slot slot) {
        Intent intent = new Intent(context, ConcertFlow.class);
        intent.setAction(CANCEL_CONCERT);
        intent.putExtra(SLOT, slot);
        return intent;
    }

    /**
     * Pack the given Slot into an Intent formatted for @code{unpackSlotInIntent} to subscribe to a concert
     * @param context the context from which the Intent is created
     * @param slot    the slot to pack in the Intent
     * @return        the slot packed in an Intent
     */
    public static Intent packSubscribeIntentWithSlot(Context context, Slot slot) {
        Intent intent = new Intent(context, ConcertFlow.class);
        intent.setAction(SUBSCRIBE_CONCERT);
        intent.putExtra(SLOT, slot);
        return intent;
    }

    /**
     * Unpack the callback function from an Intent
     * @param intent the Intent to unpack
     * @return the callback function stored in the Intent or @code{null} if the Intent wasn't properly formatted
     */
    public static AbstractConcertFlow.FlowCallback unpackCallback(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(GET_ALL_CONCERT)) {
                Bundle bundle = intent.getBundleExtra(CALLBACK);
                if (bundle != null) {
                    Object o = bundle.get(CALLBACK);
                    if (o instanceof Slot)
                        return (AbstractConcertFlow.FlowCallback) o;
                }
            }
        }
        return null;
    }

    /**
     * Pack the given callback function into an Intent formatted for @code{unpackSlotInIntent}
     * @param context  the context from which the Intent is created
     * @param callback the callback function to pack in the Intent
     * @return         the id packed in an Intent
     */
    public static Intent packCallback(Context context, AbstractConcertFlow.FlowCallback callback) {
        Intent intent = new Intent(context, ConcertFlow.class);
        intent.setAction(GET_ALL_CONCERT);
        intent.putExtra(CALLBACK, callback);
        return intent;
    }
}
