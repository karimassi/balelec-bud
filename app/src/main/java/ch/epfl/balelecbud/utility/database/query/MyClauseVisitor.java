package ch.epfl.balelecbud.utility.database.query;

public interface  MyClauseVisitor<T> {

    void visit(MyWhereClause clause);

    void visit(MyGeoClause clause);

    T build();
}
