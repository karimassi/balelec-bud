package ch.epfl.balelecbud.util.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class RefreshableRecyclerViewAdapter<A, B extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<B> {

    private final ViewHolderFactory<B> factory;
    private final RecyclerViewData<A, B> data;
    private final int itemId;

    public RefreshableRecyclerViewAdapter(ViewHolderFactory<B> factory, RecyclerViewData<A, B> data, int itemId) {
        this.factory = factory;
        this.data = data;
        this.itemId = itemId;
        data.setAdapter(this);
        data.reload();
    }

    public void reloadData(){
        data.reload();
    }

    public void setOnRefreshListener(SwipeRefreshLayout refreshLayout) {
        refreshLayout.setOnRefreshListener(() -> {
            reloadData();
            refreshLayout.setRefreshing(false);
        });
    }

    @NonNull
    @Override
    public B onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemId, parent, false);
        return factory.createInstance(view);
    }

    @Override
    public void onBindViewHolder(@NonNull B holder, int position) {
        data.bind(position, holder);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
