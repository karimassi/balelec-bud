package ch.epfl.balelecbud.Transport;

interface TransportAdapterFacade {
    void notifyItemInserted(int position);

    void notifyItemChanged(int position);

    void notifyItemRemoved(int position);
}
