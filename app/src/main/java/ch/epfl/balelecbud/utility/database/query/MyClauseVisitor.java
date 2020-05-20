package ch.epfl.balelecbud.utility.database.query;

/**
 * Interface modeling a clause visitor
 */
public interface MyClauseVisitor<T> {

    void visit(MyWhereClause clause);

    void visit(MyGeoClause clause);

    T build();
}
