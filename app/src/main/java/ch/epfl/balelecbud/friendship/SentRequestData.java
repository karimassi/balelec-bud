package ch.epfl.balelecbud.friendship;

import java.util.ArrayList;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.friendship.FriendshipUtils.getUsersFromUids;
import static ch.epfl.balelecbud.util.database.Database.DOCUMENT_ID_OPERAND;

public class SentRequestData extends RecyclerViewData<User, SentRequestViewHolder> {

    private final User currentUser;

    public SentRequestData(User currentUser) {
        super();
        this.currentUser = currentUser;
    }

    @Override
    public void reload(Database.Source preferredSource) {
        MyQuery myQuery = new MyQuery(Database.SENT_REQUESTS_PATH,
                new MyWhereClause(DOCUMENT_ID_OPERAND, MyWhereClause.Operator.EQUAL, currentUser.getUid()), preferredSource);
        getAppDatabase().query(myQuery)
                .thenApply(maps -> new ArrayList<>(maps.get(0).keySet()))
                .thenCompose(uids -> CompletableFutureUtils.unify(getUsersFromUids(uids, preferredSource)))
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
