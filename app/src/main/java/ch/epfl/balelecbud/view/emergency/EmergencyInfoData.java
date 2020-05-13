package ch.epfl.balelecbud.view.emergency;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.EmergencyInfo;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

public class EmergencyInfoData extends RecyclerViewData<EmergencyInfo, EmergencyInfoHolder> {

        @Override
        public CompletableFuture<Void> reload(Database.Source preferredSource) {
            MyQuery query = new MyQuery(Database.EMERGENCY_INFO_PATH, new LinkedList<>(), preferredSource);
            return BalelecbudApplication.getAppDatabase().query(query, EmergencyInfo.class)
                    .thenAccept(new CompletableFutureUtils.MergeConsumer<>(this));
        }

        @Override
        public void bind(int index, EmergencyInfoHolder viewHolder) {
            viewHolder.emergencyInfoNameTextView.setText(data.get(index).getName());
            viewHolder.emergencyInfoInstructionTextView.setText(data.get(index).getInstruction());
        }
}

