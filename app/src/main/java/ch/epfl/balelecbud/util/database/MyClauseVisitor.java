package ch.epfl.balelecbud.util.database;

public interface  MyClauseVisitor<T> {

    void visit(MyWhereClause clause);

    void visit(MyGeoClause clause);

    T build();
}
