package ch.epfl.balelecbud.pointOfInterest;

import android.util.Log;

import java.util.LinkedList;

import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class PointOfInterestData extends RecyclerViewData<PointOfInterest, PointOfInterestHolder> {

    @Override
    public void reload() {
        MyQuery query = new MyQuery(DatabaseWrapper.POINT_OF_INTEREST_PATH, new LinkedList<>());
        getAppDatabaseWrapper().query(query, PointOfInterest.class)
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, PointOfInterestHolder viewHolder) {
        viewHolder.nameTextView.setText(data.get(index).getName());
        viewHolder.typeTextView.setText(data.get(index).getType());
        PointOfInterestUtils.getAmountNearPointOfInterest(data.get(index)).whenComplete((integer, throwable) -> {
            if (throwable != null) {
                Log.d(PointOfInterestUtils.class.getSimpleName(), throwable.getLocalizedMessage());
            } else {
                viewHolder.amountNearPoiTextView.setText(String.valueOf(integer));
            }
        });
    }
}
