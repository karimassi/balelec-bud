package ch.epfl.balelecbud.util.database;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirestoreQueryConverter {

    public static Query convert(MyQuery myQuery){
        Query firestoreQuery = FirebaseFirestore.getInstance().collection(myQuery.getCollectionName());
        for(MyQuery.WhereClause clause : myQuery.getWhereClauses()){
            firestoreQuery = addClauseTo(firestoreQuery, clause);
        }
        return firestoreQuery;
    }

    private static Query addClauseTo(Query query, MyQuery.WhereClause clause){
        Object rightOperand = clause.getRightOperand();
        String leftOperand = clause.getLeftOperand();
        switch (clause.getOp()){
            case LESS_THAN: return query.whereLessThan(leftOperand, rightOperand);
            case LESS_EQUAL: return query.whereLessThanOrEqualTo(leftOperand, rightOperand);
            case GREATER_THAN: return query.whereGreaterThan(leftOperand, rightOperand);
            case GREATER_EQUAL: return query.whereGreaterThanOrEqualTo(leftOperand, rightOperand);
            // default is equal, necessary because java is stupid and does not see we are matching on
            // all possible values of the enum
            default: return query.whereEqualTo(leftOperand, rightOperand);
        }
    }
}
