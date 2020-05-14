package ch.epfl.balelecbud.view.friendship;

import java.util.ArrayList;

import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;

public class ReceivedFriendRequestData extends RecyclerViewData<User, ReceivedRequestViewHolder> {

    private final User currentUser;

    public ReceivedFriendRequestData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public void reload(Database.Source preferredSource) {
        MyQuery query = new MyQuery(Database.FRIEND_REQUESTS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, currentUser.getUid()));
        getAppDatabase().query(query)
                .thenApply(maps -> new ArrayList<>(maps.get(0).keySet()))
                .thenCompose(strings -> CompletableFutureUtils.unify(FriendshipUtils.getUsersFromUids(strings, preferredSource)))
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
