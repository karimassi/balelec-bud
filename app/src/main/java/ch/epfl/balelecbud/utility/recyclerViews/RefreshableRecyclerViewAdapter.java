package ch.epfl.balelecbud.utility.recyclerViews;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.DateFormatter;
import ch.epfl.balelecbud.utility.database.Database;

/**
 * Generic adapter for refreshable recycler view
 * @param <A> the type of the data displayed in the recycler view
 * @param <B> the type of the view holder for the displayed data
 */
public final class RefreshableRecyclerViewAdapter<A, B extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<B> {

    private static final String TAG = "RefRecViewAdpt";
    private final ViewHolderFactory<B> factory;
    private final View freshnessView;
    private final RecyclerViewData<A, B> data;
    private final int itemId;

    public RefreshableRecyclerViewAdapter(ViewHolderFactory<B> factory, View freshnessView, RecyclerViewData<A, B> data, int itemId) {
        this.factory = factory;
        this.freshnessView = freshnessView;
        this.data = data;
        this.itemId = itemId;
        data.setAdapter(this);
        checkConnectivityAndReload(Database.Source.CACHE_FIRST).thenAccept(this::handleFreshness);
    }

    @VisibleForTesting //could trigger it with UI in the tests, but conceptually cleaner
    public CompletableFuture<Long> reloadData() {
        return checkConnectivityAndReload(Database.Source.REMOTE_ONLY);
    }

    public void setOnRefreshListener(SwipeRefreshLayout refreshLayout) {
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            reloadData()
                    .thenAccept(this::handleFreshness)
                    .thenRun(() -> refreshLayout.setRefreshing(false));
        });
    }

    private void handleFreshness(Long freshness) {
        Log.v(TAG, "handling freshness : " + freshness);
        if (freshness == null) {
            freshnessView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.f));
        } else {
            freshnessView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
            String result = BalelecbudApplication.getAppContext().getString(R.string.cache_info) + DateFormatter.format(freshness);
            TextView textView = freshnessView.findViewById(R.id.freshness_info_text_view);
            textView.setText(result);
        }
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

    private CompletableFuture<Long> checkConnectivityAndReload(Database.Source preferredSource) {
        return BalelecbudApplication.getConnectivityChecker().isConnectionAvailable() ?
                data.reload(preferredSource) :
                data.reload(Database.Source.CACHE_ONLY);
    }
}
