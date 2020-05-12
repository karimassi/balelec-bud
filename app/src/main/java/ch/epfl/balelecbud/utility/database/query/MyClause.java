package ch.epfl.balelecbud.utility.database.query;

public interface MyClause {

    void accept(MyClauseVisitor visitor);
}
