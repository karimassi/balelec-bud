package ch.epfl.balelecbud.transport;

import java.util.List;

import ch.epfl.balelecbud.transport.objects.Transport;

public interface TransportDatabase {
    TransportListener getTransportListener(TransportAdapterFacade adapter, List<Transport> transports);
}
