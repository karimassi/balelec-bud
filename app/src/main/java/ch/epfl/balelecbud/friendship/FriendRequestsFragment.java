package ch.epfl.balelecbud.friendship;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class FriendRequestsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_friend_requests, container, false);

        Context context = view.getContext();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view_friend_requests);
        FriendRequestData data = new FriendRequestData((User)getArguments().get("user"));
        final RefreshableRecyclerViewAdapter adapter = new RefreshableRecyclerViewAdapter<>(RequestViewHolder::new, data, R.layout.item_friend_request );

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh_layout_friend_requests);
        adapter.setOnRefreshListener(refreshLayout);

        return view;
    }

    public static FriendRequestsFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable("user", user);
        FriendRequestsFragment fragment = new FriendRequestsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
