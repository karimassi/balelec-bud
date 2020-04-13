package ch.epfl.balelecbud.friendship;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class ReceivedFriendRequestData extends RecyclerViewData<User, ReceivedRequestViewHolder> {

    private final User currentUser;

    public ReceivedFriendRequestData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public void reload() {
        FriendshipUtils.getRequestsUids(currentUser)
                .thenCompose(strings -> CompletableFutureUtils.unify(FriendshipUtils.getUsersFromUids(strings)))
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(final int index, ReceivedRequestViewHolder viewHolder) {
        viewHolder.friendName.setText(data.get(index).getDisplayName());
        viewHolder.acceptButton.setOnClickListener(v -> {
            FriendshipUtils.acceptRequest(data.get(index));
            remove(index);
        });
        viewHolder.deleteButton.setOnClickListener(v -> {
            FriendshipUtils.deleteRequest(data.get(index), currentUser);
            remove(index);
        });
    }


}
