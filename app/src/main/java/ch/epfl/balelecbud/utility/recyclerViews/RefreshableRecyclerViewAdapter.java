package ch.epfl.balelecbud.utility.recyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.utility.database.Database;

public class RefreshableRecyclerViewAdapter<A, B extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<B> {

    private final ViewHolderFactory<B> factory;
    private final RecyclerViewData<A, B> data;
    private final int itemId;

    public RefreshableRecyclerViewAdapter(ViewHolderFactory<B> factory, RecyclerViewData<A, B> data, int itemId) {
        this.factory = factory;
        this.data = data;
        this.itemId = itemId;
        data.setAdapter(this);
        checkConnectivityAndReload(Database.Source.CACHE_FIRST);
    }

    @VisibleForTesting //could trigger it with UI, but conceptually cleaner
    public void reloadData(){
        checkConnectivityAndReload(Database.Source.REMOTE_ONLY);
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

    private void checkConnectivityAndReload(Database.Source preferredSource){
        if(BalelecbudApplication.getConnectivityChecker().isConnectionAvailable()){
            data.reload(preferredSource);
        }else{
            data.reload(Database.Source.CACHE_ONLY);
        }
    }

}
