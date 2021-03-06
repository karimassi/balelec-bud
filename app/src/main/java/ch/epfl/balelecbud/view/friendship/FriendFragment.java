package ch.epfl.balelecbud.view.friendship;

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
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

public final class FriendFragment extends Fragment {

    private RefreshableRecyclerViewAdapter<User, FriendViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_friends, container, true);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_friends);
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh_layout_friends);

        FriendData data = new FriendData((User) getArguments().get("user"));
        adapter = new RefreshableRecyclerViewAdapter<>(FriendViewHolder::new, refreshLayout, data, R.layout.item_friend);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        adapter.setOnRefreshListener(refreshLayout);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.reloadData();
    }

    public static FriendFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        FriendFragment fragment = new FriendFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
