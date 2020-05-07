package ch.epfl.balelecbud.friendship;

import java.util.ArrayList;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.util.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.util.database.Database.FRIENDSHIPS_PATH;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;

public class FriendData extends RecyclerViewData<User, FriendViewHolder> {

    private final User currentUser;

    public FriendData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public void reload(Database.Source preferredSource) {
        MyQuery query = new MyQuery(FRIENDSHIPS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, currentUser.getUid()));
        getAppDatabase().query(query)
                .thenApply(maps -> new ArrayList<>(maps.get(0).keySet()))
                .thenCompose(strings -> CompletableFutureUtils.unify(FriendshipUtils.getUsersFromUids(strings, preferredSource)))
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
