package ch.epfl.balelecbud.pointOfInterest;

import android.app.Activity;
import android.content.Intent;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class PointOfInterestData extends RecyclerViewData<PointOfInterest, PointOfInterestHolder> {

    private final List<Integer> lastRecordedAffluence = new LinkedList<>();
    private final Activity activity;

    public PointOfInterestData(Activity activity) {
        this.activity = activity;
    }

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
        PointOfInterest poi = data.get(index);
        viewHolder.nameTextView.setText(poi.getName());
        viewHolder.typeTextView.setText(poi.getType().toString());
        viewHolder.amountNearPoiTextView.setText(String.valueOf(lastRecordedAffluence.get(index)));
        viewHolder.goToMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, RootActivity.class);
            intent.putExtra("location", poi.getLocation());
            activity.startActivity(intent);
        });
    }

    private void postResults(List<PointOfInterestUtils.PoiAffluenceTuple> results) {
        for (PointOfInterestUtils.PoiAffluenceTuple tuple : results) {
            int index = data.size();
            lastRecordedAffluence.add(index, tuple.affluence);
            add(index, tuple.poi);
        }
    }

}
