package ch.epfl.balelecbud.friendship;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;
import static ch.epfl.balelecbud.friendship.FriendshipUtils.getUsersFromUids;

public class SentRequestData extends RecyclerViewData<User, SentRequestViewHolder> {

    private final User currentUser;

    public SentRequestData(User currentUser) {
        super();
        this.currentUser = currentUser;
    }

    @Override
    public void reload() {
        MyQuery myQuery = new MyQuery(DatabaseWrapper.FRIEND_REQUESTS_PATH,
                new MyQuery.WhereClause(currentUser.getUid(), MyQuery.WhereClause.Operator.EQUAL, true));
        getAppDatabaseWrapper().queryIds(myQuery)
                .thenCompose(uids -> CompletableFutureUtils.unify(getUsersFromUids(uids)))
                .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, SentRequestViewHolder viewHolder) {
        viewHolder.friendName.setText(data.get(index).getDisplayName());
        viewHolder.cancelButton.setOnClickListener(v -> {
            FriendshipUtils.deleteRequest(currentUser, data.get(index));
            remove(index);
        });
    }
}
