package ch.epfl.balelecbud.view.pointOfInterest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.PointOfInterest;
import ch.epfl.balelecbud.utility.PointOfInterestUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;
import ch.epfl.balelecbud.view.map.MapViewFragment;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public final class PointOfInterestData extends RecyclerViewData<PointOfInterest, PointOfInterestHolder> {

    private final List<Integer> lastRecordedAffluence = new LinkedList<>();
    private final FragmentActivity activity;

    PointOfInterestData(FragmentActivity activity) {
        this.activity = activity;
    }

    //TODO change to not have rv flicker at some point
    //TODO cached ?
    @Override
    public CompletableFuture<Long> reload(Database.Source preferredSource) {
        clearAll();
        lastRecordedAffluence.clear();
        MyQuery query = new MyQuery(Database.POINT_OF_INTEREST_PATH, new LinkedList<>(), preferredSource);
        return getAppDatabase().query(query, PointOfInterest.class)
                .thenCompose(fetchedData -> PointOfInterestUtils.computeAffluence(fetchedData.getList()))
                .thenApply(results -> {
                    this.postResults(results); return null;
                });
    }
    @Override
    public void bind(int index, PointOfInterestHolder viewHolder) {
        PointOfInterest poi = data.get(index);
        viewHolder.nameTextView.setText(poi.getName());
        viewHolder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(poi.getType().getDrawableId(), 0, 0, 0);
        viewHolder.typeTextView.setText(poi.getType().toString());
        viewHolder.amountNearPoiTextView.setText(String.valueOf(lastRecordedAffluence.get(index)));
        viewHolder.goToMapButton.setOnClickListener(v -> {
            Fragment map = MapViewFragment.newInstance();
            Bundle arguments = new Bundle();
            arguments.putParcelable("location", poi.getLocation());
            map.setArguments(arguments);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_activity_frame_layout, map, MapViewFragment.TAG)
                    .commit();
        });
    }

    private void postResults(List<PointOfInterestUtils.PoiAffluenceTuple> results) {
        for (PointOfInterestUtils.PoiAffluenceTuple tuple : results) {
            int index = data.size();
            lastRecordedAffluence.add(index, tuple.getAffluence());
            add(index, tuple.getPoi());
        }
    }
}
