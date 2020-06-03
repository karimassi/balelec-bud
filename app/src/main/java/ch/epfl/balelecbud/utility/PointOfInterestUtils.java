package ch.epfl.balelecbud.utility;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.PointOfInterest;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.database.query.MyGeoClause;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppCache;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

/**
 * Collection of methods used to get the amount of users near points of interest
 */
public final class PointOfInterestUtils {

    public static CompletableFuture<Integer> getAmountNearPointOfInterest(PointOfInterest poi, InformationSource source) {
        switch (source) {
            case CACHE_ONLY  : return getCachedAmountNearPointOfInterest(poi);
            case REMOTE_ONLY : return getRemoteAmountNearPointOfInterest(poi);
            //default is CACHE_FIRST
            default : return getCachedAmountNearPointOfInterest(poi).thenCompose( x -> {
                if(x == null){
                    return getRemoteAmountNearPointOfInterest(poi);
                }else{
                    CompletableFuture<Integer> res = new CompletableFuture<>();
                    res.complete(x);
                    return res;
                }
            });
        }
    }

    public static CompletableFuture<Integer> getCachedAmountNearPointOfInterest(PointOfInterest poi) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        result.complete(poi.getAffluence());
        return result;
    }

    public static CompletableFuture<Integer> getRemoteAmountNearPointOfInterest(PointOfInterest poi) {

        double poiLongitude = poi.getLocation().getLongitude();
        double poiLatitude = poi.getLocation().getLatitude();
        double poiRadius = poi.getRadius();

        MyGeoClause geoClause = new MyGeoClause(poiLatitude, poiLongitude, poiRadius);

        return getAppDatabase().query(new MyQuery(Database.LOCATIONS_PATH, geoClause), Location.class).thenApply(locationFetchedData -> locationFetchedData.getList().size());
    }

    public static CompletableFuture<FetchedData<PointOfInterest>> computeAffluence(FetchedData<PointOfInterest> pointOfInterests, InformationSource source) {
        List<CompletableFuture<PointOfInterest>> results = new LinkedList<>();
        for (PointOfInterest poi : pointOfInterests.getList()) {
            results.add(PointOfInterestUtils.getAmountNearPointOfInterest(poi, source).thenApply(
                    affluence -> {
                        PointOfInterest res = new PointOfInterest(poi.getName(), poi.getType(), poi.getLocation(), poi.getRadius(), affluence);
                        try{
                            getAppCache().put(Database.POINT_OF_INTEREST_PATH, String.valueOf(res.hashCode()), res);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return res;
                    }));
        }
        return CompletableFutureUtils.unify(results).thenApply(list -> new FetchedData<>(list, pointOfInterests.getFreshness()));
    }

}
