package ch.epfl.balelecbud.utility.recyclerViews;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

//Necessary to instantiate generic types in StandardRecyclerViewAdapter
public interface ViewHolderFactory<T extends RecyclerView.ViewHolder> {

    T createInstance(View view);
}
