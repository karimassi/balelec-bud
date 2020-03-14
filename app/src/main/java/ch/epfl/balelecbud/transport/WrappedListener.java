package ch.epfl.balelecbud.transport;

interface WrappedListener {
    void remove();

    void registerOuterListener(TransportListener outerListener);

}
