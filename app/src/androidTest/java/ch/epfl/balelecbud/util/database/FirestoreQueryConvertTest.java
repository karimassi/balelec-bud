package ch.epfl.balelecbud.util.database;

import com.google.common.collect.Lists;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.util.database.MyQuery.WhereClause;
import ch.epfl.balelecbud.util.database.MyQuery.WhereClause.Operator;

import static junit.framework.TestCase.assertEquals;

public class FirestoreQueryConvertTest {

    private final String collectionName = "myCollectionName";
    private final String leftOperandName = "myLeftOperandName";
    private final Integer rightOperandValue = 12;

    private final WhereClause clauseLessThan =
            new WhereClause(leftOperandName, Operator.LESS_THAN, rightOperandValue);
    private final WhereClause clauseLessEquals =
            new WhereClause(leftOperandName, Operator.LESS_EQUAL, rightOperandValue);
    private final WhereClause clauseGreaterThan =
            new WhereClause(leftOperandName, Operator.GREATER_THAN, rightOperandValue);
    private final WhereClause clauseGreaterEquals =
            new WhereClause(leftOperandName, Operator.GREATER_EQUAL, rightOperandValue);
    private final WhereClause clauseEquals =
            new WhereClause(leftOperandName, Operator.EQUAL, rightOperandValue);

    private void checkQuery(WhereClause clause, Operator operator) {
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
