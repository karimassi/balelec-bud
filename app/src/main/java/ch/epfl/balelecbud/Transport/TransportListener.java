package ch.epfl.balelecbud.Transport;

import java.util.List;

import ch.epfl.balelecbud.Transport.Object.Transport;

public class TransportListener {
    private static final String TAG = TransportListener.class.getSimpleName();

    private final TransportAdapterFacade adapter;
    private final List<Transport> transports;
    private WrappedListener inner;

    public TransportListener(TransportAdapterFacade adapter, List<Transport> transports, WrappedListener inner) {
        this.adapter = adapter;
        this.transports = transports;
        this.inner = inner;
        inner.registerOuterListener(this);
    }

    public void addTransport(Transport newTransport, int index) {
        this.transports.add(index, newTransport);
        this.adapter.notifyItemInserted(index);
    }

    public void removeTransport(int index) {
        this.transports.remove(index);
        this.adapter.notifyItemRemoved(index);
    }

    public void modifyTransport(Transport newTransport, int index) {
        this.transports.set(index, newTransport);
        this.adapter.notifyItemChanged(index);
    }

    //remove the listener
    public void remove(){
        inner.remove();
        inner = null;
    }

    //TODO add something for cancelled
}
