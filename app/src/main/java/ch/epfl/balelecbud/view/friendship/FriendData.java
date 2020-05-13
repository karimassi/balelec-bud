package ch.epfl.balelecbud.view.friendship;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.Database.FRIENDSHIPS_PATH;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;

public class FriendData extends RecyclerViewData<User, FriendViewHolder> {

    private final User currentUser;

    public FriendData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public CompletableFuture<Void> reload(Database.Source preferredSource) {
        MyQuery query = new MyQuery(FRIENDSHIPS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, currentUser.getUid()));
        return getAppDatabase().query(query)
                .thenApply(maps -> new ArrayList<>(maps.get(0).keySet()))
                .thenCompose(strings -> CompletableFutureUtils.unify(FriendshipUtils.getUsersFromUids(strings, preferredSource)))
                .thenAccept(new CompletableFutureUtils.MergeConsumer<>(this));
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
