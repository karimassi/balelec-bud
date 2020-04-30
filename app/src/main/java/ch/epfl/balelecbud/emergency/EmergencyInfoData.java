package ch.epfl.balelecbud.emergency;

import java.util.LinkedList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class EmergencyInfoData extends RecyclerViewData<EmergencyInfo, EmergencyInfoHolder> {

        @Override
        public void reload() {
            MyQuery query = new MyQuery(Database.EMERGENCY_INFO_PATH, new LinkedList<>());
            BalelecbudApplication.getAppDatabase().queryWithType(query, EmergencyInfo.class)
                    .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
        }

        @Override
        public void bind(int index, EmergencyInfoHolder viewHolder) {
            viewHolder.emergencyInfoNameTextView.setText(data.get(index).getName());
            viewHolder.emergencyInfoInstructionTextView.setText(data.get(index).getInstruction());
        }
}

