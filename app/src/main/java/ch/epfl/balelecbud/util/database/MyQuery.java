package ch.epfl.balelecbud.util.database;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class MyQuery {

    private final String collectionName;
    private final List<MyWhereClause> whereClauses;
    private final MyGeoClause geoClause;

    public MyQuery(String collectionName, MyWhereClause whereClause) {
        this(collectionName, Lists.newArrayList(whereClause));
    }

    public MyQuery(String collectionName, MyGeoClause geoClause) {
        this(collectionName, Collections.emptyList(), geoClause);
    }

    public MyQuery(String collectionName, List<MyWhereClause> whereClauses) {
        this(collectionName, whereClauses, null);
    }

    public MyQuery(String collectionName, List<MyWhereClause> whereClauses, MyGeoClause geoClause) {
        this.collectionName = collectionName;
        this.whereClauses = whereClauses;
        this.geoClause = geoClause;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<MyWhereClause> getWhereClauses() {
        return whereClauses;
    }

    public MyGeoClause getGeoClause() {
        return geoClause;
    }
}
