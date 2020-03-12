package ch.epfl.balelecbud.schedule;

public interface WrappedListener {

    void remove();

    void registerOuterListener(SlotListener outerListener);

}
