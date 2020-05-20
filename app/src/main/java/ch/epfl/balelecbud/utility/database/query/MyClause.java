package ch.epfl.balelecbud.utility.database.query;

/**
 * Interface modeling a query clause using the visitor pattern
 */
public interface MyClause {

    void accept(MyClauseVisitor visitor);
}
