package ch.epfl.balelecbud.view.friendship;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

public final class SentRequestFragment extends Fragment {

    public SentRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent_request, container, false);

        Context context = view.getContext();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view_sent_request);
        View freshnessView = view.findViewById(R.id.freshness_info_layout);

        SentRequestData data = new SentRequestData((User) getArguments().get("user"));
        final RefreshableRecyclerViewAdapter<User, SentRequestViewHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(SentRequestViewHolder::new, freshnessView, data, R.layout.item_sent_request);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh_layout_sent_requests);
        adapter.setOnRefreshListener(refreshLayout);

        return view;
    }

    public static SentRequestFragment newInstance(User user) {
        SentRequestFragment fragment = new SentRequestFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }
}
