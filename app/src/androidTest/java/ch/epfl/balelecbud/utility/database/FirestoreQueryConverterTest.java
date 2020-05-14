package ch.epfl.balelecbud.utility.database;

import com.google.common.collect.Lists;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.utility.database.query.FirestoreQueryConverter;
import ch.epfl.balelecbud.utility.database.query.MyGeoClause;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator;
import uk.co.mgbramwell.geofire.android.GeoFire;
import uk.co.mgbramwell.geofire.android.model.Distance;
import uk.co.mgbramwell.geofire.android.model.DistanceUnit;
import uk.co.mgbramwell.geofire.android.model.QueryLocation;

import static junit.framework.TestCase.assertEquals;

public class FirestoreQueryConverterTest {

    private final String collectionName = "myCollectionName";
    private final String leftOperandName = "myLeftOperandName";
    private final Integer rightOperandValue = 12;

    private final MyWhereClause clauseLessThan =
            new MyWhereClause(leftOperandName, Operator.LESS_THAN, rightOperandValue);
    private final MyWhereClause clauseLessEquals =
            new MyWhereClause(leftOperandName, Operator.LESS_EQUAL, rightOperandValue);
    private final MyWhereClause clauseGreaterThan =
            new MyWhereClause(leftOperandName, Operator.GREATER_THAN, rightOperandValue);
    private final MyWhereClause clauseGreaterEquals =
            new MyWhereClause(leftOperandName, Operator.GREATER_EQUAL, rightOperandValue);
    private final MyWhereClause clauseEquals =
            new MyWhereClause(leftOperandName, Operator.EQUAL, rightOperandValue);
    private final MyGeoClause geoClause =
            new MyGeoClause(2, 1, 3);

    private void checkQuery(MyWhereClause clause, Operator operator) {
        MyQuery myQuery = new MyQuery(collectionName, Lists.newArrayList(clause));
        Query convertedQuery = FirestoreQueryConverter.convert(myQuery);
        Query pendingQuery = FirebaseFirestore.getInstance().collection(collectionName);
        Query expectedQuery = null;
        switch (operator) {
            case LESS_THAN:
                expectedQuery = pendingQuery.whereLessThan(leftOperandName, rightOperandValue);
                break;
            case EQUAL:
                expectedQuery = pendingQuery.whereEqualTo(leftOperandName, rightOperandValue);
                break;
            case LESS_EQUAL:
                expectedQuery = pendingQuery.whereLessThanOrEqualTo(leftOperandName, rightOperandValue);
                break;
            case GREATER_THAN:
                expectedQuery = pendingQuery.whereGreaterThan(leftOperandName, rightOperandValue);
                break;
            case GREATER_EQUAL:
                expectedQuery = pendingQuery.whereGreaterThanOrEqualTo(leftOperandName, rightOperandValue);
                break;
            default:
                Assert.fail();
        }
        assertEquals(expectedQuery, convertedQuery);
    }

    private void checkQuery(MyGeoClause clause) {
        MyQuery myQuery = new MyQuery(collectionName, clause);
        Query convertedQuery = FirestoreQueryConverter.convert(myQuery);

        CollectionReference pendingQuery = FirebaseFirestore.getInstance().collection(collectionName);
        GeoFire geoFire = new GeoFire(pendingQuery);
        QueryLocation queryLocation = QueryLocation.fromDegrees(clause.getCentreLatitude(), clause.getCentreLongitude());
        Query expectedQuery = geoFire.query().whereNearTo(queryLocation, new Distance(clause.getSearchRadius(), DistanceUnit.KILOMETERS)).build();

        assertEquals(expectedQuery, convertedQuery);
    }


    @Test
    public void testCreateFirestoreQueryConverter() {
        new FirestoreQueryConverter();
    }

    @Test
    public void myGeoQueryConversionReturnsCorrectQuery() {
        checkQuery(geoClause);
    }

    @Test
    public void whereLessThanConversionReturnsCorrectQuery() {
        checkQuery(clauseLessThan, Operator.LESS_THAN);
    }

    @Test
    public void whereLessEqualsConversionReturnsCorrectQuery() {
        checkQuery(clauseLessEquals, Operator.LESS_EQUAL);
    }

    @Test
    public void whereGreaterThanConversionReturnsCorrectQuery() {
        checkQuery(clauseGreaterThan, Operator.GREATER_THAN);
    }

    @Test
    public void whereGreaterEqualsConversionReturnsCorrectQuery() {
        checkQuery(clauseGreaterEquals, Operator.GREATER_EQUAL);
    }

    @Test
    public void whereEqualsConversionReturnsCorrectQuery() {
        checkQuery(clauseEquals, Operator.EQUAL);
    }
}
