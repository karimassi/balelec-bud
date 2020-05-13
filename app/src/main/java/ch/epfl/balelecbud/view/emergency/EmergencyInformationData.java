package ch.epfl.balelecbud.view.emergency;

import java.util.LinkedList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.EmergencyInformation;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

public class EmergencyInformationData extends RecyclerViewData<EmergencyInformation, EmergencyInformationHolder> {

    @Override
    public void reload(Database.Source preferredSource) {
        MyQuery query = new MyQuery(Database.EMERGENCY_INFO_PATH, new LinkedList<>(), preferredSource);
        BalelecbudApplication.getAppDatabase().query(query, EmergencyInformation.class)
                .thenApply(emergencyInfos -> {
                    emergencyInfos.removeIf(emergencyInformation -> emergencyInformation.isEmergencyNumber());
                    return emergencyInfos;
                })
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, EmergencyInformationHolder viewHolder) {
        viewHolder.emergencyInfoNameTextView.setText(data.get(index).getName());
        viewHolder.emergencyInfoInstructionTextView.setText(data.get(index).getInstruction());
    }
}

