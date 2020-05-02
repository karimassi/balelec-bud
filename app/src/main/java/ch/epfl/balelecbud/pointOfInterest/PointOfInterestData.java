package ch.epfl.balelecbud.pointOfInterest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.map.MapViewFragment;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public class PointOfInterestData extends RecyclerViewData<PointOfInterest, PointOfInterestHolder> {

    private final List<Integer> lastRecordedAffluence = new LinkedList<>();
    private final FragmentActivity activity;

    public PointOfInterestData(FragmentActivity activity) {
        this.activity = activity;
    }

    //TODO change to not have rv flicker at some point
    @Override
    public void reload() {
        clearAll();
        lastRecordedAffluence.clear();
        MyQuery query = new MyQuery(Database.POINT_OF_INTEREST_PATH, new LinkedList<>());
        getAppDatabase().queryWithType(query, PointOfInterest.class)
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
            lastRecordedAffluence.add(index, tuple.affluence);
            add(index, tuple.poi);
        }
    }

}
