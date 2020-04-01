package ch.epfl.balelecbud.util.views;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class RecyclerViewData <A, B extends RecyclerView.ViewHolder> {

    protected List<A> data = new LinkedList<>();
    private RecyclerView.Adapter<B> rvAdapter;

    public void setAdapter(RecyclerView.Adapter<B> adapter){
        this.rvAdapter = adapter;
    }

    public abstract void reload();

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

    public List<A> getData() {
        return Collections.unmodifiableList(data);
    }

    public int size(){
        return data.size();
    }

}
