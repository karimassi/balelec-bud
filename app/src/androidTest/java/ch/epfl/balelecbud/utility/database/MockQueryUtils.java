package ch.epfl.balelecbud.utility.database;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.utility.database.query.MyGeoClause;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

import static ch.epfl.balelecbud.utility.location.LocationUtils.distanceToCenter;

public class MockQueryUtils {

    public static boolean queryContainsDocumentIdClause(MyQuery query) {
        for (MyWhereClause clause : query.getWhereClauses()) {
            if (clause.getLeftOperand().equals(Database.DOCUMENT_ID_OPERAND)) {
                return true;
            }
        }
        return false;
    }

    public static String getRightOperandFromDocumentIdClause(MyQuery query) {
        for (MyWhereClause clause : query.getWhereClauses()) {
            if (clause.getLeftOperand().equals(Database.DOCUMENT_ID_OPERAND)) {
                return (String) clause.getRightOperand();
            }
        }
        throw new IllegalArgumentException("Query doesn't contain a documentId clause.");
    }


    public static <A> List<A> filterList(List<A> list, MyWhereClause clause) {
        List<A> newList = new LinkedList<>();
        if (list.isEmpty())
            return list;
        A head = list.get(0);
        Class clazz = head.getClass();
        Field field = getField(clause, clazz);
        Object rightOperand = clause.getRightOperand();
        // check that the field and our value have the same type,
        // right now they must be exactly the same class
        if (!rightOperand.getClass().equals(field.getType())) {
            return newList;
        }
        for (A elem : list) {
            filterElem(clause.getOp(), newList, field, rightOperand, elem);
        }
        return newList;
    }

    public static <A> List<A> filterWithGeoClause(List<A> list, MyGeoClause clause) {
        if(list.isEmpty()) {
            return list;
        }
        List<A> result = new LinkedList<>();
        Location centerLocation = new Location(clause.getCentreLatitude(), clause.getCentreLongitude());
        for(A elem : list) {
            if(distanceToCenter(centerLocation, (Location) elem) <= clause.getSearchRadius()) {
                result.add(elem);
            }
        }
        return result;
    }

    private static <A> void filterElem(MyWhereClause.Operator op, List<A> newList, Field field, Object rightOperand, A elem) {
        try {
            if (op == MyWhereClause.Operator.EQUAL) {
                if (Objects.equals(field.get(elem), rightOperand)) {
                    newList.add(elem);
                }
            } else if (field.get(elem) instanceof Comparable) {
                Comparable rightOperandComparable = (Comparable) field.get(elem);
                // fine because we checked before that the types were equal, would be nice
                // to find a way so that the IDE doesn't complain
                int result = rightOperandComparable.compareTo(rightOperand);
                if (evaluateResult(op, result)) {
                    newList.add(elem);
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Given field param cannot be accessed");
        }
    }

    @NonNull
    private static Field getField(MyWhereClause clause, Class clazz) {
        Field field;
        try {
            field = clazz.getDeclaredField(clause.getLeftOperand());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Class" + clazz.getName() + " does not contain a " + clause.getLeftOperand());
        }
        // to allow us to modify variable
        field.setAccessible(true);
        return field;
    }

    private static boolean evaluateResult(MyWhereClause.Operator op, int resultOfComparison) {
        switch (op) {
            case LESS_THAN:
                return resultOfComparison < 0;
            case LESS_EQUAL:
                return resultOfComparison <= 0;
            case GREATER_THAN:
                return resultOfComparison > 0;
            case GREATER_EQUAL:
                return resultOfComparison >= 0;
            default:
                return resultOfComparison == 0;
        }
    }
}
