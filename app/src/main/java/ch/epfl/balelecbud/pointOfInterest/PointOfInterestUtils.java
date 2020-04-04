package ch.epfl.balelecbud.pointOfInterest;

import androidx.annotation.VisibleForTesting;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;

public class PointOfInterestUtils {

    private static final double DISTANCE = 1.;
    private static DatabaseWrapper dbImplementation = FirestoreDatabaseWrapper.getInstance();

    @VisibleForTesting
    public static void setDbImplementation(DatabaseWrapper dbImplementation) {
        PointOfInterestUtils.dbImplementation = dbImplementation;
    }

    public static CompletableFuture<Integer> getAmountNearPointOfInterest(PointOfInterest poi){
        double poiLongitude = poi.getLocation().getLongitude();
        double poiLatitude = poi.getLocation().getLatitude();
        double minLatitude = poiLatitude - DISTANCE;
        double maxLatitude = poiLatitude + DISTANCE;
        double minLongitude = poiLongitude - DISTANCE;
        double maxLongitude = poiLongitude + DISTANCE;
        List<MyQuery.WhereClause> clauses = new LinkedList<>();
        clauses.add(new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.GREATER_EQUAL, minLongitude));
        clauses.add(new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.GREATER_EQUAL, minLatitude));
        clauses.add(new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.LESS_EQUAL, maxLongitude));
        clauses.add(new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.LESS_EQUAL, maxLatitude));
        MyQuery query = new MyQuery(DatabaseWrapper.LOCATIONS_PATH, clauses);
        CompletableFuture<List<Location>> myFuture = dbImplementation.query(query, Location.class);
        return myFuture.thenApply(List::size);
    }

}
