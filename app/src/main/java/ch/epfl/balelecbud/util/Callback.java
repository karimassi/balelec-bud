package ch.epfl.balelecbud.util;

public interface Callback<T> {

    void onSuccess(T data);

    void onFailure(String message);

}
