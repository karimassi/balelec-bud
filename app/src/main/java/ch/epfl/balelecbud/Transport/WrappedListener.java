package ch.epfl.balelecbud.Transport;

interface WrappedListener {
    void remove();

    void registerOuterListener(TransportListener outerListener);

}
