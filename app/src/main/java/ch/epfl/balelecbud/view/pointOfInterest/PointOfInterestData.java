package ch.epfl.balelecbud.view.pointOfInterest;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.PointOfInterest;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.PointOfInterestUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;
import ch.epfl.balelecbud.view.map.MapViewFragment;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public final class PointOfInterestData extends RecyclerViewData<PointOfInterest, PointOfInterestHolder> {

    private static String TAG = PointOfInterestData.class.getSimpleName();

    private final List<Integer> lastRecordedAffluence = new LinkedList<>();
    private final FragmentActivity activity;

    PointOfInterestData(FragmentActivity activity) {
        this.activity = activity;
    }

    //TODO change to not have rv flicker at some point
    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        MyQuery query = new MyQuery(Database.POINT_OF_INTEREST_PATH, new LinkedList<>(), preferredSource);
        return getAppDatabase().query(query, PointOfInterest.class)
                .thenCompose(fetchedData -> PointOfInterestUtils.computeAffluence(fetchedData, preferredSource))
                .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
    }

    @Override
    public void bind(int index, PointOfInterestHolder viewHolder) {
        PointOfInterest poi = data.get(index);
        viewHolder.nameTextView.setText(poi.getName());
        viewHolder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(poi.getType().getDrawableId(), 0, 0, 0);
        viewHolder.amountNearPoiTextView.setText(poi.getAffluence() == null ? "?" : String.valueOf(poi.getAffluence()));
        viewHolder.goToMapButton.setOnClickListener(v -> {
            Log.d(TAG, "Going to map centered on poi");
            Fragment map = MapViewFragment.newInstance();
            Bundle arguments = new Bundle();
            arguments.putParcelable("location", poi.getLocation());
            map.setArguments(arguments);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_activity_frame_layout, map, MapViewFragment.TAG)
                    .commit();
        });
    }
}
