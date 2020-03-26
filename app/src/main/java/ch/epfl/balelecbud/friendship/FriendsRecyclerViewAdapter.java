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

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder> {

    private List<User> friends = new ArrayList<>();

    public FriendsRecyclerViewAdapter() {
        reloadData();
    }

    public void reloadData() {
        FriendshipUtils.getFriendsUids(FirebaseAuthenticator.getInstance().getCurrentUser())
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
                                    for (User u : friends) {
                                        if (toAdd.isEmpty() || !toAdd.contains(u)) {
                                            removeItem(friends.indexOf(u));
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
        if (!friends.contains(user)) {
            friends.add(user);
            notifyItemInserted(friends.size()-1);
        }
    }

    private void removeItem(int position) {
        friends.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, friends.size());

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.friendName.setText(friends.get(position).getDisplayName());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendshipUtils.removeFriend(friends.get(position));
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView friendName;
        public Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.text_view_friend_item_name);
            deleteButton = itemView.findViewById(R.id.buttonDeleteFriendItem);
        }
    }


}
