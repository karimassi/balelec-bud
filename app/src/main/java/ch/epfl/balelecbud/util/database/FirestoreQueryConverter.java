package ch.epfl.balelecbud.util.database;

import com.google.firebase.firestore.Query;

public class FirestoreQueryConverter {

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
