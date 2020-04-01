package ch.epfl.balelecbud.pointOfInterest;

import android.view.InflateException;

import com.google.common.collect.Lists;
import com.google.firebase.firestore.GeoPoint;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.PointOfInterest;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;

import static junit.framework.TestCase.assertEquals;


public class PointOfInterestUtilsTest {

    private static List<Location> locations;
    private static MockDatabaseWrapper db;
    private static Location l1 = new Location(1, 1);
    private static Location l2 = new Location(2, 1);
    private static Location l3 = new Location(1, 2);
    private static Location l4 = new Location(2, 2);

    @BeforeClass
    public static void setup(){
        locations = Lists.newArrayList(l1, l2, l3, l4);
        db = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
        db.resetDocument(DatabaseWrapper.LOCATIONS_PATH);
        int i = 0;
        for(Object obj : locations){
            db.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, Integer.toString(i++), obj);
        }
    }

    private List<Location> getResList(double value, MyQuery.WhereClause.Operator op, String fieldName) throws InterruptedException, ExecutionException {
        MyQuery.WhereClause clause = new MyQuery.WhereClause(fieldName, op, value);
        MyQuery query = new MyQuery(DatabaseWrapper.LOCATIONS_PATH, Lists.newArrayList(clause));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        return result.get();
    }

    @Test
    public void queryFiltersEqualsThan() throws ExecutionException, InterruptedException {
        List<Location> resList = getResList(2, MyQuery.WhereClause.Operator.EQUAL, "longitude");
        List<Location> expected = Lists.newArrayList(l2, l4);
        assertEquals(resList, expected);
    }


    @Test
    public void queryFiltersLessThan() throws ExecutionException, InterruptedException {
        List<Location> resList = getResList(2, MyQuery.WhereClause.Operator.LESS_THAN, "longitude");
        List<Location> expected = Lists.newArrayList(l1, l3);
        assertEquals(resList, expected);
    }
    @Test
    public void queryFiltersLessEquals() throws ExecutionException, InterruptedException {
        List<Location> resList = getResList(1.5, MyQuery.WhereClause.Operator.LESS_EQUAL, "longitude");
        List<Location> expected = Lists.newArrayList(l1, l3);
        assertEquals(resList, expected);
    }

    @Test
    public void queryFiltersGreaterThan() throws ExecutionException, InterruptedException {
        List<Location> resList = getResList(1, MyQuery.WhereClause.Operator.GREATER_THAN, "longitude");
        List<Location> expected = Lists.newArrayList(l2, l4);
        assertEquals(resList, expected);
    }

    @Test
    public void queryFiltersGreaterEquals() throws ExecutionException, InterruptedException {
        List<Location> resList = getResList(1.5, MyQuery.WhereClause.Operator.GREATER_EQUAL, "longitude");
        List<Location> expected = Lists.newArrayList(l2, l4);
        assertEquals(resList, expected);
    }

    @Test
    public void queryCompoundsMultipleClauses() throws ExecutionException, InterruptedException {
        MyQuery.WhereClause clause1 = new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.GREATER_THAN, (double) 1);
        MyQuery.WhereClause clause2 = new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.GREATER_THAN, (double) 1);
        MyQuery.WhereClause clause3 = new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.EQUAL, (double) 2);
        MyQuery query = new MyQuery(DatabaseWrapper.LOCATIONS_PATH, Lists.newArrayList(clause1, clause2));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        List<Location> resList = result.get();
        List<Location> expected = Lists.newArrayList(l4);
        assertEquals(resList, expected);
    }

    @Test
    public void getAmountNearPOIreturnsExpectedAmount() throws ExecutionException, InterruptedException {
        db.resetDocument(DatabaseWrapper.LOCATIONS_PATH);

        for(int y = 0; y < 5; ++y){
            for(int x = 0; x < 5; ++x){
                db.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, Integer.toString(y * 5 + x), new Location(x,y));
            }
        }

        PointOfInterestUtils.setDbImplementation(db);

        PointOfInterest p1 = new PointOfInterest(new GeoPoint(1,1), "whatever", "also whatever", "mega whatever");
        PointOfInterest p2 = new PointOfInterest(new GeoPoint(4,4), "whatever", "also whatever", "mega whatever");

        int res1 = PointOfInterestUtils.getAmountNearPointOfInterest(p1).get();
        int res2 = PointOfInterestUtils.getAmountNearPointOfInterest(p2).get();

        assertEquals(9, res1);
        assertEquals(4, res2);
    }


}
