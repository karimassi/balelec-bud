package ch.epfl.balelecbud.friendship;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.models.User;

public class FriendRequestsRecyclerViewAdapter extends RecyclerView.Adapter<FriendRequestsRecyclerViewAdapter.ViewHolder> {

    private List<User> requests = new ArrayList<>();

    public FriendRequestsRecyclerViewAdapter() {
        reloadData();
    }

    public void reloadData() {
        FriendshipUtils.getRequestsUids(FirebaseAuthenticator.getInstance().getCurrentUser())
                .whenComplete(new BiConsumer<List<String>, Throwable>() {
                    @Override
                    public void accept(List<String> strings, Throwable throwable) {
                        if (strings != null) {
                            final List<CompletableFuture<User>> cfs = FriendshipUtils.getFriends(strings);
                            CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).whenComplete(new BiConsumer<Void, Throwable>() {
                                @Override
                                public void accept(Void aVoid, Throwable throwable) {
                                    List<User> toAdd = new ArrayList<>();
                                    for (CompletableFuture<User> cf : cfs) {
                                        toAdd.add(cf.getNow(null)); // should never be null since Future waited for all of
                                    }
                                    for (User u : requests) {
                                        if (toAdd.isEmpty() || !toAdd.contains(u)) {
                                            removeItem(requests.indexOf(u));
                                        }
                                    }
                                    for (User u : toAdd) {
                                        addItem(u);
                                    }

                                }
                            });
                        }
                    }
                });
    }

    private void addItem(User user) {
        if (!requests.contains(user)) {
            requests.add(user);
            notifyItemInserted(requests.size()-1);
        }
    }

    private void removeItem(int position) {
        requests.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, requests.size());

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new FriendRequestsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.friendName.setText(requests.get(position).getDisplayName());
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendshipUtils.acceptRequest(requests.get(position));
                removeItem(position);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendshipUtils.deleteRequest(requests.get(position));
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView friendName;
        public Button acceptButton;
        public Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.text_view_friend_request_item_name);
            acceptButton = itemView.findViewById(R.id.button_request_item_accept_request);
            deleteButton = itemView.findViewById(R.id.button_request_item_delete_request);

        }
    }


}
