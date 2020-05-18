package ch.epfl.balelecbud.utility;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.PointOfInterest;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyGeoClause;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

/**
 * Collection of methods used to get the amount of users near points of interest
 */
public final class PointOfInterestUtils {

    public static CompletableFuture<Integer> getAmountNearPointOfInterest(PointOfInterest poi) {

        double poiLongitude = poi.getLocation().getLongitude();
        double poiLatitude = poi.getLocation().getLatitude();
        double poiRadius = poi.getRadius();

        MyGeoClause geoClause = new MyGeoClause(poiLatitude, poiLongitude, poiRadius);

        return getAppDatabase().query(new MyQuery(Database.LOCATIONS_PATH, geoClause), Location.class).thenApply(locationFetchedData -> locationFetchedData.getList().size());
    }

    public static CompletableFuture<List<PointOfInterestUtils.PoiAffluenceTuple>> computeAffluence(List<PointOfInterest> pointOfInterests) {
        List<CompletableFuture<PointOfInterestUtils.PoiAffluenceTuple>> results = new LinkedList<>();
        for (PointOfInterest poi : pointOfInterests) {
            results.add(PointOfInterestUtils.getAmountNearPointOfInterest(poi).thenApply(affluence -> new PointOfInterestUtils.PoiAffluenceTuple(poi, affluence)));
        }
        return CompletableFutureUtils.unify(results);
    }

   public static class PoiAffluenceTuple {

        final PointOfInterest poi;
        final int affluence;

        PoiAffluenceTuple(PointOfInterest poi, int affluence) {
            this.poi = poi;
            this.affluence = affluence;
        }

       public int getAffluence() {
           return affluence;
       }

       public PointOfInterest getPoi() {
           return poi;
       }
   }
}
