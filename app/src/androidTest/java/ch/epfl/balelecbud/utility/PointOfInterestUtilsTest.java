package ch.epfl.balelecbud.utility;

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
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.PointOfInterest;
import ch.epfl.balelecbud.model.PointOfInterestType;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

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
        CompletableFuture<FetchedData<Location>> result = db.query(query, Location.class);
        return result.get().getList();
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
    public void testGetRemoteAmountNearPointOfInterest() throws ExecutionException, InterruptedException {
        db.resetDocument(Database.LOCATIONS_PATH);
        ArrayList<Location> locations = Lists.newArrayList(
                new Location(46.51808, 6.56905),
                new Location(46.51809, 6.56909),
                new Location(4651810, 6.56919),
                new Location(33, 3));

        int index = 0;
        for (Location loc : locations) {
            db.storeDocumentWithID(Database.LOCATIONS_PATH, Integer.toString(index++), loc);
        }

        BalelecbudApplication.setAppDatabase(db);

        PointOfInterest p = new PointOfInterest("whatever", PointOfInterestType.BAR,
                new Location(46.51808,6.56906), 0.003);

        int res = PointOfInterestUtils.getRemoteAmountNearPointOfInterest(p).get();

        assertEquals(2, res);
    }

    @Test
    public void defaultConstructor() {
        new PointOfInterestUtils();
    }
}
