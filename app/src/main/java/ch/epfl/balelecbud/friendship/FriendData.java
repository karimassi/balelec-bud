package ch.epfl.balelecbud.friendship;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class FriendData extends RecyclerViewData<User, FriendViewHolder> {

    private final User currentUser;

    public FriendData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public void reload() {
        FriendshipUtils.getFriendsUids(currentUser)
                .thenCompose(strings -> CompletableFutureUtils.unify(FriendshipUtils.getUsersFromUids(strings)))
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(final int index, FriendViewHolder viewHolder) {
        viewHolder.friendName.setText(data.get(index).getDisplayName());
        viewHolder.deleteButton.setOnClickListener(v -> {
            FriendshipUtils.removeFriend(data.get(index));
            remove(index);
        });
    }
}
