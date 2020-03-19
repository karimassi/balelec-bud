package ch.epfl.balelecbud.util.facades;

public interface RecyclerViewAdapterFacade {

    void notifyItemInserted(int position);
    void notifyItemChanged(int position);
    void notifyItemRemoved(int position);

}
