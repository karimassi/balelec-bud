package ch.epfl.balelecbud.Transport;

import java.util.List;

import ch.epfl.balelecbud.Transport.Object.Transport;

public class TransportProvider {
    static TransportListener subscribeTransport(TransportDatabase db, TransportAdapterFacade adapter, List<Transport> transports){
        return db.getTransportListener(adapter, transports);
    }
}
