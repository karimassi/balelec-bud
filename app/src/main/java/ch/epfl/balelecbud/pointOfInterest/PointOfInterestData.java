package ch.epfl.balelecbud.pointOfInterest;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class PointOfInterestData extends RecyclerViewData<PointOfInterest, PointOfInterestHolder> {

    private final List<Integer> lastRecordedAffluence = new LinkedList<>();

    //TODO change to not have rv flicker at some point
    @Override
    public void reload() {
        clearAll();
        lastRecordedAffluence.clear();
        MyQuery query = new MyQuery(DatabaseWrapper.POINT_OF_INTEREST_PATH, new LinkedList<>());
        getAppDatabaseWrapper().query(query, PointOfInterest.class)
                .thenCompose(PointOfInterestUtils::computeAffluence)
                .thenAccept(this::postResults);
    }

    @Override
    public void bind(int index, PointOfInterestHolder viewHolder) {
        viewHolder.nameTextView.setText(data.get(index).getName());
        viewHolder.typeTextView.setText(data.get(index).getType().toString());
        viewHolder.amountNearPoiTextView.setText(String.valueOf(lastRecordedAffluence.get(index)));
    }

    private void postResults(List<PointOfInterestUtils.PoiAffluenceTuple> results) {
        for (PointOfInterestUtils.PoiAffluenceTuple tuple : results) {
            int index = data.size();
            lastRecordedAffluence.add(index, tuple.affluence);
            add(index, tuple.poi);
        }
    }

}
