package ch.epfl.balelecbud.view.friendship;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.FetchedData;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;

public final class ReceivedFriendRequestData extends RecyclerViewData<User, ReceivedRequestViewHolder> {

    private static String TAG = ReceivedFriendRequestsFragment.class.getSimpleName();

    private final User currentUser;

    ReceivedFriendRequestData(User user) {
        super();
        currentUser = user;
    }

    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        MyQuery query = new MyQuery(Database.FRIEND_REQUESTS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, currentUser.getUid()));
        return getAppDatabase().query(query)
                .thenApply(fetchedData -> new ArrayList<>(fetchedData.getList().get(0).keySet()))
                .thenCompose(strings -> CompletableFutureUtils.unify(FriendshipUtils.getUsersFromUids(strings, InformationSource.REMOTE_ONLY)))
                //Wrap in a FetchedData with a freshness set to null
                .thenApply(FetchedData::new)
                .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
    }

    @Override
    public void bind(final int index, ReceivedRequestViewHolder viewHolder) {
        viewHolder.friendName.setText(data.get(index).getDisplayName());
        viewHolder.friendEmail.setText(data.get(index).getEmail());
        viewHolder.acceptButton.setOnClickListener(v -> {
            FriendshipUtils.acceptRequest(data.get(index));
            remove(index);
            Log.d(TAG, "Accepted request from: " + data.get(index).getDisplayName());
        });
        viewHolder.deleteButton.setOnClickListener(v -> {
            FriendshipUtils.deleteRequest(data.get(index), currentUser);
            remove(index);
            Log.d(TAG, "Rejected request from: " + data.get(index).getDisplayName());
        });
    }
}
