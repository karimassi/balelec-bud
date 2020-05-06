package ch.epfl.balelecbud.pointOfInterest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;

import static junit.framework.TestCase.assertEquals;

public class PointOfInterestUtilsTest {

    private final MockDatabase db = MockDatabase.getInstance();
    private final static Location l1 = new Location(1, 1);
    private final static Location l2 = new Location(1, 2);
    private final static Location l3 = new Location(2, 1);
    private final static Location l4 = new Location(2, 2);

    @Before
    public void setup(){
        db.resetDatabase();
        List<Location> locations = Lists.newArrayList(l1, l2, l3, l4);
        db.resetDocument(Database.LOCATIONS_PATH);
        int i = 0;
        for(Object obj : locations){
            db.storeDocumentWithID(Database.LOCATIONS_PATH, Integer.toString(i++), obj);
        }
    }

    private List<Location> getResList(List<MyWhereClause> clauses)
            throws InterruptedException, ExecutionException {
        MyQuery query = new MyQuery(Database.LOCATIONS_PATH, clauses);
        CompletableFuture<List<Location>> result = db.queryWithType(query, Location.class);
        return result.get();
    }

    private void checkFilter(List<MyWhereClause> clauses, Location... expected)
            throws InterruptedException, ExecutionException {
        Set<Location> res = Sets.newHashSet(getResList(clauses));
        Set<Location> expectedSet = Sets.newHashSet(expected);
        assertEquals(expectedSet, res);
    }

    @Test
    public void queryFiltersEqualsThan() throws ExecutionException, InterruptedException {
        checkFilter(Collections.singletonList(new MyWhereClause(
                "longitude", MyWhereClause.Operator.EQUAL, (double) 2))
                , l2, l4);
    }

    @Test
    public void queryFiltersLessThan() throws ExecutionException, InterruptedException {
        checkFilter(Collections.singletonList(new MyWhereClause(
                        "longitude", MyWhereClause.Operator.LESS_THAN, (double) 2))
                , l1, l3);
    }

    @Test
    public void queryFiltersLessEquals() throws ExecutionException, InterruptedException {
        checkFilter(Collections.singletonList(new MyWhereClause(
                        "longitude", MyWhereClause.Operator.LESS_EQUAL, (double) 2))
                , l1, l2, l3, l4);
    }

    @Test
    public void queryFiltersGreaterThan() throws ExecutionException, InterruptedException {
        checkFilter(Collections.singletonList(new MyWhereClause(
                        "longitude", MyWhereClause.Operator.GREATER_THAN, (double) 1))
                , l2, l4);
    }

    @Test
    public void queryFiltersGreaterEquals() throws ExecutionException, InterruptedException {
        checkFilter(Collections.singletonList(new MyWhereClause(
                        "longitude", MyWhereClause.Operator.GREATER_EQUAL, (double) 1))
                , l1, l2, l3, l4);
    }

    @Test
    public void queryCompoundsMultipleClauses() throws ExecutionException, InterruptedException {
        MyWhereClause clause1 =
                new MyWhereClause("longitude", MyWhereClause.Operator.GREATER_THAN, (double) 1);
        MyWhereClause clause2 =
                new MyWhereClause("latitude", MyWhereClause.Operator.GREATER_THAN, (double) 1);
        checkFilter(Arrays.asList(clause1, clause2), l4);
    }

    @Test
    public void getAmountNearPOIReturnsExpectedAmount() throws ExecutionException, InterruptedException {
        db.resetDocument(Database.LOCATIONS_PATH);
        ArrayList<Location> locations = Lists.newArrayList(new Location(46.51812, 6.56900),
                new Location(46.51814, 6.56911));

        int index = 0;
        for (Location loc : locations) {
            db.storeDocumentWithID(Database.LOCATIONS_PATH, Integer.toString(index++), loc);
        }

        BalelecbudApplication.setAppDatabase(db);

        PointOfInterest p1 = new PointOfInterest(new Location(46.51808,6.56906),
                "whatever", PointOfInterestType.STAGE);

        int res1 = PointOfInterestUtils.getAmountNearPointOfInterest(p1).get();

        assertEquals(2, res1);
    }

    @Test
    public void defaultConstructor() {
        new PointOfInterestUtils();
    }
}
