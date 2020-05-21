package ch.epfl.balelecbud.utility.database.query;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.database.Database;

/**
 * Class modeling normal clause
 */
public final class MyQuery {

    private final String collectionName;
    private final List<MyWhereClause> whereClauses;
    private final MyGeoClause geoClause;
    private final InformationSource source;

    public MyQuery(String collectionName, MyWhereClause whereClause, InformationSource source) {
        this(collectionName, Lists.newArrayList(whereClause), source);
    }

    public MyQuery(String collectionName, MyWhereClause whereClause) {
        this(collectionName, whereClause, InformationSource.REMOTE_ONLY);
    }

    public MyQuery(String collectionName, MyGeoClause geoClause) {
        this(collectionName, Collections.emptyList(), geoClause, InformationSource.REMOTE_ONLY);
    }

    public MyQuery(String collectionName, MyGeoClause geoClause, InformationSource source) {
        this(collectionName, Collections.emptyList(), geoClause, source);
    }

    public MyQuery(String collectionName, List<MyWhereClause> whereClauses) {
        this(collectionName, whereClauses, null, InformationSource.REMOTE_ONLY);
    }

    public MyQuery(String collectionName, List<MyWhereClause> whereClauses, InformationSource source) {
        this(collectionName, whereClauses, null, source);
    }

    public MyQuery(String collectionName, List<MyWhereClause> whereClauses, MyGeoClause geoClause, InformationSource source) {
        this.collectionName = collectionName;
        this.whereClauses = whereClauses;
        this.geoClause = geoClause;
        this.source = source;
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

    public boolean hasDocumentIdOperand() {
        for (MyWhereClause clause: getWhereClauses()) {
            if (clause.getLeftOperand().equals(Database.DOCUMENT_ID_OPERAND)) {
                return true;
            }
        }
        return false;
    }

    public String getIdOperand() {
        for (MyWhereClause clause: getWhereClauses()) {
            if (clause.getLeftOperand().equals(Database.DOCUMENT_ID_OPERAND)) {
                return (String) clause.getRightOperand();
            }
        }
        throw new IllegalArgumentException();
    }

    public InformationSource getSource() {
        return source;
    }
}
