package ch.epfl.balelecbud.friendship;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class FriendRequestData extends RecyclerViewData<User, RequestViewHolder> {

    private User currentUser;

    public FriendRequestData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public void reload() {
        FriendshipUtils.getRequestsUids(currentUser)
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
                                    for (User u : data) {
                                        if (toAdd.isEmpty() || !toAdd.contains(u)) {
                                            remove(data.indexOf(u));
                                        }
                                    }
                                    for (User u : toAdd) {
                                        if(!data.contains(u))
                                            add(data.size(), u);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void bind(final int index, RequestViewHolder viewHolder) {
        viewHolder.friendName.setText(data.get(index).getDisplayName());
        viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendshipUtils.acceptRequest(data.get(index));
                remove(index);
            }
        });
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendshipUtils.deleteRequest(data.get(index));
                remove(index);
            }
        });
    }


}
