package ch.epfl.balelecbud.pointOfInterest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class PointOfInterestUtils {

    private static final double RADIUS_METERS = 1.;
    private static final double EARTH_RADIUS_METERS = 6371000.;

    public static CompletableFuture<Integer> getAmountNearPointOfInterest(PointOfInterest poi){
        double poiLongitude = poi.getLocation().getLongitude();
        double poiLatitude = poi.getLocation().getLatitude();

        double dY = 360 * RADIUS_METERS / EARTH_RADIUS_METERS;
        double dX = dY * Math.cos(Math.toRadians(poiLatitude));

        double minLatitude = poiLatitude - dY;
        double maxLatitude = poiLatitude + dY;
        double minLongitude = poiLongitude - dX;
        double maxLongitude = poiLongitude + dX;
        List<MyQuery.WhereClause> clauses = new LinkedList<>();
        clauses.add(new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.GREATER_EQUAL, minLongitude));
        clauses.add(new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.GREATER_EQUAL, minLatitude));
        clauses.add(new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.LESS_EQUAL, maxLongitude));
        clauses.add(new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.LESS_EQUAL, maxLatitude));
        MyQuery query = new MyQuery(DatabaseWrapper.LOCATIONS_PATH, clauses);
        CompletableFuture<List<Location>> myFuture = getAppDatabaseWrapper().query(query, Location.class);
        return myFuture.thenApply(List::size);
    }
}
