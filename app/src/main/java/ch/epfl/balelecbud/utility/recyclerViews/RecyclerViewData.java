package ch.epfl.balelecbud.utility.recyclerViews;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.utility.database.Database;

public abstract class RecyclerViewData <A, B extends RecyclerView.ViewHolder> {

    protected final List<A> data = new LinkedList<>();
    private RecyclerView.Adapter<B> rvAdapter;

    public void setAdapter(RecyclerView.Adapter<B> adapter){
        this.rvAdapter = adapter;
    }

    public abstract void reload(Database.Source preferredSource);

    public abstract void bind(int index, B viewHolder);

    public void add(int index, A newElem){
        data.add(index, newElem);
        rvAdapter.notifyItemInserted(index);
    }

    public void remove(int index){
        data.remove(index);
        rvAdapter.notifyItemRemoved(index);
        rvAdapter.notifyItemRangeChanged(index, data.size());
    }

    public void clearAll(){
        int oldSize = data.size();
        data.clear();
        rvAdapter.notifyItemRangeRemoved(0, oldSize);
    }

    public List<A> getData() {
        return Collections.unmodifiableList(new LinkedList<>(data));
    }

    public int size(){
        return data.size();
    }

}
