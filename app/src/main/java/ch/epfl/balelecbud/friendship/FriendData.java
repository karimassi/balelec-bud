package ch.epfl.balelecbud.friendship;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class FriendData extends RecyclerViewData<User, FriendViewHolder> {

    private User currentUser;

    public FriendData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public void reload() {
        FriendshipUtils.getFriendsUids(currentUser)
                .thenCompose( strings -> CompletableFutureUtils.unify(FriendshipUtils.getFriends(strings)))
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<User>(this));
        /*
        FriendshipUtils.getFriendsUids(currentUser)
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
                });*/
    }

    @Override
    public void bind(final int index, FriendViewHolder viewHolder) {
        viewHolder.friendName.setText(data.get(index).getDisplayName());
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendshipUtils.removeFriend(data.get(index));
                remove(index);
            }
        });
    }
}
