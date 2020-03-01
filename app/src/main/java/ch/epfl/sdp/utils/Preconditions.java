package ch.epfl.sdp.utils;

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
}
