package ch.epfl.balelecbud.transport;

interface TransportAdapterFacade {
    void notifyItemInserted(int position);

    void notifyItemChanged(int position);

    void notifyItemRemoved(int position);
}
