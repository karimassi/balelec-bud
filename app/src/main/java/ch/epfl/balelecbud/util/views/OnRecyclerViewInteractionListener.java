package ch.epfl.balelecbud.util.views;

import ch.epfl.balelecbud.transport.objects.TransportStation;

public interface OnRecyclerViewInteractionListener<T> {

    void onItemSelected(T item);

}
