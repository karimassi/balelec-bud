package ch.epfl.balelecbud.util.adapters;

public interface RecyclerViewAdapterFacade {

    void notifyItemInserted(int position);
    void notifyItemChanged(int position);
    void notifyItemRemoved(int position);

}
