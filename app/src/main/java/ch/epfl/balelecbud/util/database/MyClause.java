package ch.epfl.balelecbud.util.database;

public interface MyClause {

    void accept(MyClauseVisitor visitor);
}
