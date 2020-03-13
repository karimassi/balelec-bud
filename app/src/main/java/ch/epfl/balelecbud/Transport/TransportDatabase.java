package ch.epfl.balelecbud.Transport;

import java.util.List;

import ch.epfl.balelecbud.Transport.Object.Transport;

public interface TransportDatabase {
    TransportListener getTransportListener(TransportAdapterFacade adapter, List<Transport> transports);
}
