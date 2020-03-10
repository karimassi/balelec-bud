package ch.epfl.balelecbud.Transport;

import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.Transport.Object.Transport;

public class TransportListener {
    private static final String TAG = TransportListener.class.getSimpleName();

    private final TransportAdapterFacade adapter;
    private final List<Transport> transports;
    private final List<String> transportsIds;
    private WrappedListener inner;

    public TransportListener(TransportAdapterFacade adapter, List<Transport> transports, WrappedListener inner) {
        this.adapter = adapter;
        this.transports = transports;
        this.transportsIds = new LinkedList<>();
        this.inner = inner;
        inner.registerOuterListener(this);
    }


    public void transportAdded(Transport newTransport, String newTransportKey) {
        //should log everything at some point
        transportsIds.add(newTransportKey);
        this.transports.add(newTransport);
        adapter.notifyItemInserted(this.transports.size() - 1);
    }

    public void transportChanged(Transport updatedTransport, String updatedTransportKey) {
        int index = transportsIds.indexOf(updatedTransportKey);
        if(index != -1){
            this.transports.set(index, updatedTransport);
            adapter.notifyItemChanged(index);
        }else{
            Log.w(TAG, "child " + updatedTransportKey +" changed but was not tracked to start with");
        }
    }

    public void transportRemoved(String removedTransportKey) {
        int index = transportsIds.indexOf(removedTransportKey);
        if(index != -1){
            transportsIds.remove(index);
            this.transports.remove(index);
            adapter.notifyItemRemoved(index);
        }else{
            Log.w(TAG, "child " + removedTransportKey +" deleted but was not tracked to start with");
        }
    }

    //remove the listener
    public void remove(){
        inner.remove();
    }

    //used for testing for now
    public List<String> getTransportsIds() {
        return Collections.unmodifiableList(transportsIds);
    }

    //TODO add something for cancelled
}
