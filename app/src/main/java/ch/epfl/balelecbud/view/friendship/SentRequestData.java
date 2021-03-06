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
import static ch.epfl.balelecbud.utility.FriendshipUtils.getUsersFromUids;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;

public final class SentRequestData extends RecyclerViewData<User, SentRequestViewHolder> {

    private static String TAG = SentRequestData.class.getSimpleName();

    private final User currentUser;

    SentRequestData(User currentUser) {
        super();
        this.currentUser = currentUser;
    }

    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        MyQuery myQuery = new MyQuery(Database.SENT_REQUESTS_PATH,
                new MyWhereClause(DOCUMENT_ID_OPERAND, MyWhereClause.Operator.EQUAL, currentUser.getUid()));
        return getAppDatabase().query(myQuery)
                .thenApply(fetchedData -> new ArrayList<>(fetchedData.getList().get(0).keySet()))
                .thenCompose(uids -> CompletableFutureUtils.unify(getUsersFromUids(uids, InformationSource.REMOTE_ONLY)))
                .thenApply(FetchedData::new)
                .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
    }

    @Override
    public void bind(int index, SentRequestViewHolder viewHolder) {
        viewHolder.friendName.setText(data.get(index).getDisplayName());
        viewHolder.friendEmail.setText(data.get(index).getEmail());
        viewHolder.cancelButton.setOnClickListener(v -> {
            FriendshipUtils.deleteRequest(currentUser, data.get(index));
            remove(index);
            Log.d(TAG, "Deleted request");
        });
    }
}
