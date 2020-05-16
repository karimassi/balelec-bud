package ch.epfl.balelecbud.utility.database.query;

import com.google.firebase.firestore.Query;

/**
 * A utility class used to convert queries into Firestore queries
 */
public class FirestoreQueryConverter {

    /**
     * Convert the given query into a Firestore query
     *
     * @param myQuery a query
     * @return        the converted query
     */
    public static Query convert(MyQuery myQuery) {

        FirestoreClauseConverter visitor = new FirestoreClauseConverter(myQuery.getCollectionName());
        if (myQuery.getGeoClause() != null) {
            myQuery.getGeoClause().accept(visitor);
        }

        for (MyClause clause : myQuery.getWhereClauses()) {
            clause.accept(visitor);
        }

        return visitor.build();
    }
}
