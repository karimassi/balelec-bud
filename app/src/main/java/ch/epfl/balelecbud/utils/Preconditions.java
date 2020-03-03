package ch.epfl.balelecbud.utils;

public class Preconditions {

    public static void checkArgument(boolean condition){
        if(!condition){
            throw new IllegalArgumentException();
        }    }

    public static void checkArgument(boolean condition, String message){
        if(!condition){
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNonNull(Object obj){
        if(obj == null){
            throw new NullPointerException();
        }
    }
}
