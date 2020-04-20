package ch.epfl.balelecbud.friendship;

import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class FriendshipUtils {

    public static void addFriend(User friend) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(getAppAuthenticator().getCurrentUser().getUid(), true);
        getAppDatabaseWrapper().storeDocumentWithID(DatabaseWrapper.FRIEND_REQUESTS_PATH, friend.getUid(), toStore);
    }

    public static void removeFriend(User friend) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(friend.getUid(), FieldValue.delete());
        getAppDatabaseWrapper().updateDocument(DatabaseWrapper.FRIENDSHIPS_PATH,
                getAppAuthenticator().getCurrentUser().getUid(), updates);

        updates = new HashMap<>();
        updates.put(getAppAuthenticator().getCurrentUser().getUid(), FieldValue.delete());
        getAppDatabaseWrapper().updateDocument(DatabaseWrapper.FRIENDSHIPS_PATH, friend.getUid(), updates);
    }

    public static void acceptRequest(User sender) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(sender.getUid(), true);
        getAppDatabaseWrapper().storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH,
                getAppAuthenticator().getCurrentUser().getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(getAppAuthenticator().getCurrentUser().getUid(), true);
        getAppDatabaseWrapper().storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH, sender.getUid(), toStore);

        deleteRequest(sender, getAppAuthenticator().getCurrentUser());
    }

    public static void deleteRequest(User sender, User receiver) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(sender.getUid(), FieldValue.delete());
        getAppDatabaseWrapper().updateDocument(DatabaseWrapper.FRIEND_REQUESTS_PATH,
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
        return getAppDatabaseWrapper().getCustomDocument(DatabaseWrapper.USERS_PATH, uid, User.class);
    }

    public static CompletableFuture<User> getUserFromEmail(String email) {
        return getAppDatabaseWrapper()
                .getDocumentWithFieldCondition(DatabaseWrapper.USERS_PATH, "email", email, User.class);
    }

    public static CompletableFuture<List<String>> getRequestsUids(User user) {
        return getUidsFromCollection(user, DatabaseWrapper.FRIEND_REQUESTS_PATH);
    }

    public static CompletableFuture<List<String>> getFriendsUids(User user) {
        return getUidsFromCollection(user, DatabaseWrapper.FRIENDSHIPS_PATH);
    }

    private static CompletableFuture<List<String>> getUidsFromCollection(User user, String collectionName) {
        return getAppDatabaseWrapper().getDocument(collectionName, user.getUid())
                .thenApply(stringObjectMap -> new ArrayList<>(stringObjectMap.keySet()));
    }
}