package ch.epfl.balelecbud.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ch.epfl.balelecbud.utility.notifications.concertFlow.ConcertFlow;
import ch.epfl.balelecbud.model.Slot;

public final class FlowUtils {
    private static final String TAG = "ch.epfl.balelecbud.util.notifications.concertFlow";
    public static final String CANCEL_CONCERT = TAG + ".CANCEL_CONCERT";
    public static final String SUBSCRIBE_CONCERT =  TAG + ".SUBSCRIBE_CONCERT";
    public static final String ACK_CONCERT = TAG + ".ACK_CONCERT";
    public static final String GET_ALL_CONCERT = TAG + ".GET_ALL_CONCERT";
    private static final String RECEIVE_ALL_CONCERT = TAG + ".RECEIVE_ALL_CONCERT";
    private static final String ID = TAG + ".ID";
    private static final String SLOT = TAG + ".SLOT";
    private static final String CALLBACK = TAG + ".CALLBACK";
    public static final String CALLBACK_INTENT = TAG + ".CALLBACK_INTENT";

    /**
     * Unpack the given Intent to retrieve the Id stored in it
     *
     * @param intent the Intent to unpack
     * @return the Id stored in the Intent or @code{-1} if the Intent wasn't properly formatted
     */
    public static int unpackIdFromAckIntent(@NonNull Intent intent) {
        if (ACK_CONCERT.equals(intent.getAction())) {
            return intent.getIntExtra(ID, -1);
        } else {
            return -1;
        }
    }

    /**
     * Pack the given Id into an Intent formatted for @code{unpackIdInIntent}
     *
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
    public static Slot unpackSlotFromIntent(@NonNull Intent intent) {
        String action = intent.getAction();
        if (SUBSCRIBE_CONCERT.equals(action) || CANCEL_CONCERT.equals(action)) {
            Parcelable p = intent.getParcelableExtra(SLOT);
            if (p instanceof Slot)
                return (Slot) p;
        }
        return null;
    }

    /**
     * Pack the given Slot into an Intent formatted for @code{unpackSlotInIntent} to cancel a concert
     *
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
     *
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
     *
     * @param slots  the slots to pack into the intent
     * @param intent the intent to store the slots into
     * @return       the given intent with the slots packed in it
     */
    @NonNull
    public static Intent packCallback(ArrayList<Slot> slots, @NonNull Intent intent) {
        intent.setAction(RECEIVE_ALL_CONCERT);
        intent.putParcelableArrayListExtra(CALLBACK, slots);
        return intent;
    }

    /**
     * Unpack the array of Slot stored in the given Intent
     *
     * @param intent the intent to retrieve the array from
     * @return       a list of the slots stored in the intent or null if the Intent wasn't properly formatted
     */
    public static ArrayList<Slot> unpackCallback(@NonNull Intent intent) {
        if (RECEIVE_ALL_CONCERT.equals(intent.getAction())) {
            return intent.getParcelableArrayListExtra(CALLBACK);
        }
        return null;
    }

    /**
     * Unpack the given Intent to retrieve the Intent stored in it
     *
     * @param intent the Intent to unpack
     * @return       the Intent stored in the given Intent or @code{null} if the Intent wasn't properly formatted
     */
    public static Intent unpackIntentFromIntent(@NonNull Intent intent) {
        if (GET_ALL_CONCERT.equals(intent.getAction())) {
            Parcelable p = intent.getParcelableExtra(CALLBACK_INTENT);
            if (p instanceof Intent)
                return (Intent) p;
        }
        return null;
    }
}
