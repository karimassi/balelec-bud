package ch.epfl.balelecbud.util.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import uk.co.mgbramwell.geofire.android.GeoFire;
import uk.co.mgbramwell.geofire.android.model.Distance;
import uk.co.mgbramwell.geofire.android.model.DistanceUnit;
import uk.co.mgbramwell.geofire.android.model.QueryLocation;

public class FirestoreClauseConverter implements MyClauseVisitor<Query> {

    private Query queryToCreate;
    private String collectionName;
    private boolean geoClauseVisited = false;

    public FirestoreClauseConverter(String collectionName) {
        this.collectionName = collectionName;
        queryToCreate = FirebaseFirestore.getInstance().collection(collectionName);
    }

    @Override
    public void visit(MyWhereClause clause) {
        Object rightOperand = clause.getRightOperand();
        String leftOperand = clause.getLeftOperand();
        switch (clause.getOp()) {
            case LESS_THAN:
                queryToCreate = queryToCreate.whereLessThan(leftOperand, rightOperand);
                break;
            case LESS_EQUAL:
                queryToCreate = queryToCreate.whereLessThanOrEqualTo(leftOperand, rightOperand);
                break;

            case GREATER_THAN:
                queryToCreate = queryToCreate.whereGreaterThan(leftOperand, rightOperand);
                break;
            case GREATER_EQUAL:
                queryToCreate = queryToCreate.whereGreaterThanOrEqualTo(leftOperand, rightOperand);
                break;
            // default is equal, necessary because java is stupid and does not see we are matching on
            // all possible values of the enum
            default:
                queryToCreate = queryToCreate.whereEqualTo(leftOperand, rightOperand);
        }
    }


    @Override
    //must be called first and only once
    public void visit(MyGeoClause clause) {
        if (geoClauseVisited) {
            throw new IllegalStateException("visitng a GeoClause can be done only once per visitor");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myCollection = db.collection(collectionName);
        GeoFire geoFire = new GeoFire(myCollection);

        QueryLocation queryLocation = QueryLocation.fromDegrees(clause.getCentreLatitude(), clause.getCentreLongitude());
        Distance searchDistance = new Distance(clause.getSearchRadius(), DistanceUnit.KILOMETERS);

        queryToCreate = geoFire.query().whereNearTo(queryLocation, searchDistance).build();
        geoClauseVisited = true;
    }

    public Query build() {
        return queryToCreate;
    }
}
