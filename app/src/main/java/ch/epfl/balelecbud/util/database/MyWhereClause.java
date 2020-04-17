package ch.epfl.balelecbud.util.database;

public class MyWhereClause implements MyClause {

    private final Operator op;
    private final Object rightOperand;
    private final String leftOperand;

    public MyWhereClause(String leftOperand, Operator op, Object rightOperand) {
        this.op = op;
        this.rightOperand = rightOperand;
        this.leftOperand = leftOperand;
    }

    public Operator getOp() {
        return op;
    }

    public Object getRightOperand() {
        return rightOperand;
    }

    public String getLeftOperand() {
        return leftOperand;
    }

    @Override
    public void accept(MyClauseVisitor visitor) {
        visitor.visit(this);
    }

    public enum Operator {
        LESS_THAN,
        LESS_EQUAL,
        GREATER_THAN,
        GREATER_EQUAL,
        EQUAL
    }



}
