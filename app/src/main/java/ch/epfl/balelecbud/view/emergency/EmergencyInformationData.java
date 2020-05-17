package ch.epfl.balelecbud.view.emergency;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.EmergencyInformation;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

/**
 *
 */
public final class EmergencyInformationData extends RecyclerViewData<EmergencyInformation, EmergencyInformationHolder> {

    private boolean needNumbers;
    private OnRecyclerViewInteractionListener<EmergencyInformation> interactionListener;

    EmergencyInformationData(boolean needNumbers, OnRecyclerViewInteractionListener<EmergencyInformation> interactionListener) {
        this.needNumbers = needNumbers;
        this.interactionListener = interactionListener;
    }

    @Override
    public CompletableFuture<Long> reload(Database.Source preferredSource) {
        MyQuery query = new MyQuery(Database.EMERGENCY_INFO_PATH, new LinkedList<>(), preferredSource);
        return BalelecbudApplication.getAppDatabase().query(query, EmergencyInformation.class)
                .thenApply(emergencyInfos -> {
                    emergencyInfos.getList().removeIf(emergencyInformation -> needNumbers != emergencyInformation.isEmergencyNumber());
                    return emergencyInfos;
                })
                .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
    }

    @Override
    public void bind(int index, EmergencyInformationHolder viewHolder) {
        viewHolder.emergencyInfoNameTextView.setText(data.get(index).getName());
        viewHolder.emergencyInfoInstructionTextView.setText(data.get(index).getInstruction());
        if (needNumbers && interactionListener != null) {
            viewHolder.itemView.setOnClickListener(v -> interactionListener.onItemSelected(data.get(index)));
        }
    }
}

