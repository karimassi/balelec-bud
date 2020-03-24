package ch.epfl.balelecbud.notifications.concertFlow;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

public final class FlowUtil {
    private static final String TAG = "ch.epfl.balelecbud.notifications.concertFlow";
    public static final String CANCEL_CONCERT = TAG + ".CANCEL_CONCERT";
    public static final String SUBSCRIBE_CONCERT =  TAG + ".SUBSCRIBE_CONCERT";
    public static final String ACK_CONCERT = TAG + ".ACK_CONCERT";
    public static final String GET_ALL_CONCERT = TAG + ".GET_ALL_CONCERT";
    public static final String RECEIVE_ALL_CONCERT = TAG + ".RECEIVE_ALL_CONCERT";
    public static final String ID = TAG + ".ID";
    public static final String SLOT = TAG + ".SLOT";
    public static final String CALLBACK = TAG + ".CALLBACK";
    public static final String CALLBACK_INTENT = TAG + ".CALLBACK_INTENT";

    /**
     * Unpack the given Intent to retrieve the Id stored in it
     *
     * @param intent the Intent to unpack
     * @return the Id stored in the Intent or @code{-1} if the Intent wasn't properly formatted
     */
    public static int unpackIdInIntent(Intent intent) {
        if (intent != null && ACK_CONCERT.equals(intent.getAction())) {
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
    @NonNull
    public static Intent packAckIntentWithId(Context context, int id) {
        Intent intent = new Intent(context, ConcertFlow.class);
        intent.setAction(ACK_CONCERT);
        intent.putExtra(ID, id);
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
            if (SUBSCRIBE_CONCERT.equals(action) || CANCEL_CONCERT.equals(action)) {
                Parcelable p = intent.getParcelableExtra(SLOT);
                if (p instanceof Slot)
                    return (Slot) p;
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
    @NonNull
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
    @NonNull
    public static Intent packSubscribeIntentWithSlot(Context context, Slot slot) {
        Intent intent = new Intent(context, ConcertFlow.class);
        intent.setAction(SUBSCRIBE_CONCERT);
        intent.putExtra(SLOT, slot);
        return intent;
    }

    /**
     * Pack the given list of Slot into the given Intent
     * @param slots  the slots to pack into the intent
     * @param intent the intent to store the slots into
     * @return       the given intent with the slots packed in it
     */
    @NonNull
    public static Intent packCallback(Slot[] slots, @NonNull Intent intent) {
        intent.setAction(RECEIVE_ALL_CONCERT);
        intent.putExtra(CALLBACK, slots);
        return intent;
    }

    /**
     * Unpack the array of Slot stored in the given Intent
     * @param intent the intent to retrieve the array from
     * @return       the array stored in the intent or null if the Intent wasn't properly formatted
     */
    public static List<Slot> unpackCallback(Intent intent) {
        if (intent != null && RECEIVE_ALL_CONCERT.equals(intent.getAction())) {
            Parcelable[] ps = intent.getParcelableArrayExtra(CALLBACK);
            if (ps != null) {
                Log.d("FlowUtil", "unpackCallback: ps = " + Arrays.toString(ps));
                List<Slot> slots = new LinkedList<>();
                for (Parcelable p : ps) {
                    if (p instanceof Slot)
                        slots.add((Slot) p);
                }
                return slots;
            }
        }
        return null;
    }

    /**
     * Unpack the given Intent to retrieve the Intent stored in it
     *
     * @param intent the Intent to unpack
     * @return the Intent stored in the given Intent or @code{null} if the Intent wasn't properly formatted
     */
    public static Intent unpackIntentInIntent(Intent intent) {
        if (intent != null && GET_ALL_CONCERT.equals(intent.getAction())) {
            Parcelable p = intent.getParcelableExtra(CALLBACK_INTENT);
            if (p instanceof Intent)
                return (Intent) p;
        }
        return null;
    }
}
