package ch.epfl.balelecbud.util;

public interface Callback {

    void onSuccess();

    void onFailure(String message);

}
