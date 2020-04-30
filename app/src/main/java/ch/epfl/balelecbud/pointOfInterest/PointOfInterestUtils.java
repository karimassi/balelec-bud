package ch.epfl.balelecbud.pointOfInterest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyGeoClause;
import ch.epfl.balelecbud.util.database.MyQuery;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public class PointOfInterestUtils {

    private static final double SEARCH_RADIUS_IN_KM = 0.003;

    public static CompletableFuture<Integer> getAmountNearPointOfInterest(PointOfInterest poi) {

        double poiLongitude = poi.getLocation().getLongitude();
        double poiLatitude = poi.getLocation().getLatitude();

        MyGeoClause geoClause = new MyGeoClause(poiLatitude, poiLongitude, SEARCH_RADIUS_IN_KM);

        return getAppDatabase().queryWithType(new MyQuery(Database.LOCATIONS_PATH, geoClause), Location.class).thenApply(List::size);
    }

    public static CompletableFuture<List<PointOfInterestUtils.PoiAffluenceTuple>> computeAffluence(List<PointOfInterest> pointOfInterests) {
        List<CompletableFuture<PointOfInterestUtils.PoiAffluenceTuple>> results = new LinkedList<>();
        for (PointOfInterest poi : pointOfInterests) {
            results.add(PointOfInterestUtils.getAmountNearPointOfInterest(poi).thenApply(affluence -> new PointOfInterestUtils.PoiAffluenceTuple(poi, affluence)));
        }
        return CompletableFutureUtils.unify(results);
    }

    static class PoiAffluenceTuple {

        final PointOfInterest poi;
        final int affluence;

        PoiAffluenceTuple(PointOfInterest poi, int affluence) {
            this.poi = poi;
            this.affluence = affluence;
        }
    }
}
