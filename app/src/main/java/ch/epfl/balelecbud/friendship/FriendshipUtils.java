package ch.epfl.balelecbud.friendship;

import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.util.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;

public class FriendshipUtils {

    public static void addFriend(User friend) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(getAppAuthenticator().getCurrentUser().getUid(), true);
        getAppDatabase().storeDocumentWithID(Database.FRIEND_REQUESTS_PATH, friend.getUid(), toStore);
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
    }

    public static void deleteRequest(User sender, User receiver) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(sender.getUid(), FieldValue.delete());
        getAppDatabase().updateDocument(Database.FRIEND_REQUESTS_PATH,
                receiver.getUid(), updates);
    }

    public static List<CompletableFuture<User>> getUsersFromUids(List<String> uidList) {
        final List<CompletableFuture<User>> cfs = new ArrayList<>();
        for (String uid : uidList) {
            cfs.add(FriendshipUtils.getUserFromUid(uid));
        }
        return cfs;
    }

    public static CompletableFuture<User> getUserFromUid(String uid) {
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, uid));
        return getAppDatabase().queryWithType(query, User.class).thenApply(users -> users.get(0));
    }

    public static CompletableFuture<User> getUserFromEmail(String email) {
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause("email", EQUAL, email));
        return getAppDatabase()
                .queryWithType(query, User.class).thenApply(users -> users.get(0));
    }

    public static CompletableFuture<List<String>> getRequestsUids(User user) {
        return getUidsFromCollection(user, Database.FRIEND_REQUESTS_PATH);
    }

    public static CompletableFuture<List<String>> getFriendsUids(User user) {
        return getUidsFromCollection(user, Database.FRIENDSHIPS_PATH);
    }

    private static CompletableFuture<List<String>> getUidsFromCollection(User user, String collectionName) {
        MyQuery query = new MyQuery(collectionName, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, user.getUid()));
        return getAppDatabase().query(query).thenApply(maps -> new ArrayList<>(maps.get(0).keySet()));
    }
}