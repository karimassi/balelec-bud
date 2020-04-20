package ch.epfl.balelecbud.util.database;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.util.StringUtils;

public class MyQuery {

    private final String collectionName;
    private final List<WhereClause> whereClauses;

    public MyQuery(String collectionName, WhereClause whereClause){
        this(collectionName, Lists.newArrayList(whereClause));
    }

    public MyQuery(String collectionName, List<WhereClause> whereClauses) {
        this.collectionName = collectionName;
        this.whereClauses = whereClauses;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<WhereClause> getWhereClauses() {
        return whereClauses;
    }

    public static class WhereClause {

        private final Operator op;
        private final Object rightOperand;
        private final String leftOperand;

        public WhereClause(String leftOperand, Operator op, Object rightOperand) {
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

        public enum Operator {
            LESS_THAN,
            LESS_EQUAL,
            GREATER_THAN,
            GREATER_EQUAL,
            EQUAL
        }

    }


}
