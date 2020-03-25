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
        db = new MockDatabaseWrapper();
        List<Object> objectList = new LinkedList<>();
        objectList.addAll(locations);
        db.setListToQuery(objectList);
    }

    @Test
    public void queryFiltersEqualsThan() throws ExecutionException, InterruptedException {
        MyQuery.WhereClause clause = new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.EQUAL, (double) 2);
        MyQuery query = new MyQuery("whatever", Lists.newArrayList(clause));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        List<Location> resList = result.get();
        List<Location> expected = Lists.newArrayList(l2, l4);
        assertEquals(resList, expected);
    }
    @Test
    public void queryFiltersLessThan() throws ExecutionException, InterruptedException {
        MyQuery.WhereClause clause = new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.LESS_THAN, (double) 2);
        MyQuery query = new MyQuery("whatever", Lists.newArrayList(clause));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        List<Location> resList = result.get();
        List<Location> expected = Lists.newArrayList(l1, l3);
        assertEquals(resList, expected);
    }
    @Test
    public void queryFiltersLessEquals() throws ExecutionException, InterruptedException {
        MyQuery.WhereClause clause = new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.LESS_EQUAL, 1.5);
        MyQuery query = new MyQuery("whatever", Lists.newArrayList(clause));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        List<Location> resList = result.get();
        List<Location> expected = Lists.newArrayList(l1, l3);
        assertEquals(resList, expected);
    }

    @Test
    public void queryFiltersGreaterThan() throws ExecutionException, InterruptedException {
        MyQuery.WhereClause clause = new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.GREATER_THAN, (double) 1);
        MyQuery query = new MyQuery("whatever", Lists.newArrayList(clause));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        List<Location> resList = result.get();
        List<Location> expected = Lists.newArrayList(l2, l4);
        assertEquals(resList, expected);
    }

    @Test
    public void queryFiltersGreaterEquals() throws ExecutionException, InterruptedException {
        MyQuery.WhereClause clause = new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.GREATER_EQUAL, 1.5);
        MyQuery query = new MyQuery("whatever", Lists.newArrayList(clause));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        List<Location> resList = result.get();
        List<Location> expected = Lists.newArrayList(l2, l4);
        assertEquals(resList, expected);
    }

    @Test
    public void queryCompoundsMultipleClauses() throws ExecutionException, InterruptedException {
        MyQuery.WhereClause clause1 = new MyQuery.WhereClause("longitude", MyQuery.WhereClause.Operator.GREATER_THAN, (double) 1);
        MyQuery.WhereClause clause2 = new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.GREATER_THAN, (double) 1);
        MyQuery.WhereClause clause3 = new MyQuery.WhereClause("latitude", MyQuery.WhereClause.Operator.EQUAL, (double) 2);
        MyQuery query = new MyQuery("whatever", Lists.newArrayList(clause1, clause2));
        CompletableFuture<List<Location>> result = db.query(query, Location.class);
        List<Location> resList = result.get();
        List<Location> expected = Lists.newArrayList(l4);
        assertEquals(resList, expected);
    }

    @Test
    public void getAmountNearPOIreturnsExpectedAmount() throws ExecutionException, InterruptedException {
        List<Object> grid = new LinkedList<>();
        for(int y = 0; y < 5; ++y){
            for(int x = 0; x < 5; ++x){
                grid.add(new Location(x,y));
            }
        }
        db.setListToQuery(grid);
        PointOfInterestUtils.setDbImplementation(db);

        PointOfInterest p1 = new PointOfInterest(new GeoPoint(1,1), "whatever", "also whatever", "mega whatever");
        PointOfInterest p2 = new PointOfInterest(new GeoPoint(4,4), "whatever", "also whatever", "mega whatever");

        int res1 = PointOfInterestUtils.getAmountNearPointOfInterest(p1).get();
        int res2 = PointOfInterestUtils.getAmountNearPointOfInterest(p2).get();

        assertEquals(9, res1);
        assertEquals(4, res2);
    }


}
