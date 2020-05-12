package ch.epfl.balelecbud.utility;

import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.cloudMessaging.Message;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.Database.FRIENDSHIPS_PATH;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;

public class FriendshipUtils {

    public static void addFriend(User friend) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(getAppAuthenticator().getCurrentUser().getUid(), true);
        getAppDatabase().storeDocumentWithID(Database.FRIEND_REQUESTS_PATH, friend.getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(friend.getUid(), true);
        getAppDatabase().storeDocumentWithID(Database.SENT_REQUESTS_PATH, getAppAuthenticator().getCurrentUser().getUid(), toStore);

        Message.sendFriendshipMessage(getAppAuthenticator().getCurrentUser(), friend.getUid(),
                getAppContext().getString(R.string.type_friend_request));
    }

    public static void removeFriend(User friend) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(friend.getUid(), FieldValue.delete());
        getAppDatabase().updateDocument(Database.FRIENDSHIPS_PATH,
                getAppAuthenticator().getCurrentUser().getUid(), updates);

        updates = new HashMap<>();
        updates.put(getAppAuthenticator().getCurrentUser().getUid(), FieldValue.delete());
        getAppDatabase().updateDocument(Database.FRIENDSHIPS_PATH, friend.getUid(), updates);
    }

    public static void acceptRequest(User sender) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(sender.getUid(), true);
        getAppDatabase().storeDocumentWithID(Database.FRIENDSHIPS_PATH,
                getAppAuthenticator().getCurrentUser().getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(getAppAuthenticator().getCurrentUser().getUid(), true);
        getAppDatabase().storeDocumentWithID(Database.FRIENDSHIPS_PATH, sender.getUid(), toStore);

        deleteRequest(sender, getAppAuthenticator().getCurrentUser());

        Message.sendFriendshipMessage(getAppAuthenticator().getCurrentUser(), sender.getUid(),
                getAppContext().getString(R.string.type_accept_request));
    }

    public static void deleteRequest(User sender, User receiver) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(sender.getUid(), FieldValue.delete());
        getAppDatabase().updateDocument(Database.FRIEND_REQUESTS_PATH,
                receiver.getUid(), updates);

        updates = new HashMap<>();
        updates.put(receiver.getUid(), FieldValue.delete());
        getAppDatabase().updateDocument(Database.SENT_REQUESTS_PATH,
                sender.getUid(), updates);
    }

    public static List<CompletableFuture<User>> getUsersFromUids(List<String> uidList, Database.Source preferredSource) {
        final List<CompletableFuture<User>> cfs = new ArrayList<>();
        for (String uid : uidList) {
            cfs.add(FriendshipUtils.getUserFromUid(uid, preferredSource));
        }
        return cfs;
    }

    public static CompletableFuture<User> getUserFromUid(String uid, Database.Source preferredSource) {
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, uid), preferredSource);
        return getAppDatabase().query(query, User.class).thenApply(users -> users.get(0));
    }

    public static CompletableFuture<User> getUserFromEmail(String email, Database.Source preferredSource) {
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause("email", EQUAL, email), preferredSource);
        return getAppDatabase()
                .query(query, User.class).thenApply(users -> users.get(0));
    }

    public static CompletableFuture<List<String>> getFriendsUids(User user) {
        MyQuery query = new MyQuery(FRIENDSHIPS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, user.getUid()));
        return getAppDatabase().query(query).thenApply(maps -> new ArrayList<>(maps.get(0).keySet()));
    }
}