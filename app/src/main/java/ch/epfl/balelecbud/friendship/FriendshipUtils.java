package ch.epfl.balelecbud.friendship;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class FriendshipUtils {

    private static DatabaseWrapper database = FirestoreDatabaseWrapper.getInstance();
    private static Authenticator auth = FirebaseAuthenticator.getInstance();

    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper) {
        database = databaseWrapper;
    }

    @VisibleForTesting
    public static void setAuthenticator(Authenticator authenticator) {
        auth = authenticator;
    }

    public static void addFriend(User friend) {
        Map<String,Boolean> toStore = new HashMap<>();
        toStore.put(auth.getCurrentUser().getUid(), true);
        database.storeDocumentWithID(DatabaseWrapper.FRIEND_REQUESTS_PATH, friend.getUid(), toStore);
    }

    public static void removeFriend(User friend) {
        Map<String,Object> updates = new HashMap<>();
        updates.put(friend.getUid(), FieldValue.delete());
        database.updateDocument(DatabaseWrapper.FRIENDSHIPS_PATH, auth.getCurrentUser().getUid(), updates);

        updates = new HashMap<>();
        updates.put(auth.getCurrentUser().getUid(), FieldValue.delete());
        database.updateDocument(DatabaseWrapper.FRIENDSHIPS_PATH, friend.getUid(), updates);
    }

    public static void acceptRequest(User sender) {
        Map<String,Boolean> toStore = new HashMap<>();
        toStore.put(sender.getUid(), true);
        database.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH, auth.getCurrentUser().getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(auth.getCurrentUser().getUid(), true);
        database.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH, sender.getUid(), toStore);

        deleteRequest(sender);
    }

    public static void deleteRequest(User sender) {
        Map<String,Object> updates = new HashMap<>();
        updates.put(sender.getUid(), FieldValue.delete());
        database.updateDocument(DatabaseWrapper.FRIEND_REQUESTS_PATH, auth.getCurrentUser().getUid(), updates);
    }

    public static List<CompletableFuture<User>> getFriends(List<String> uidList) {
        final List<CompletableFuture<User>> cfs = new ArrayList<>();
        for (String uid : uidList) {
            cfs.add(FriendshipUtils.getUserFromUid(uid));
        }
        return cfs;
    }

    public static CompletableFuture<User> getUserFromUid(String uid) {
        return database.getCustomDocument(DatabaseWrapper.USERS_PATH, uid, User.class);
    }

    public static CompletableFuture<User> getUserFromEmail(String email) {
        return database.getDocumentWithFieldCondition(DatabaseWrapper.USERS_PATH, "email", email, User.class);
    }

    public static CompletableFuture<List<String>> getRequestsUids(User user) {
        return getUidsFromCollection(user, DatabaseWrapper.FRIEND_REQUESTS_PATH);
    }

    public static CompletableFuture<List<String>> getFriendsUids(User user) {
        return getUidsFromCollection(user, DatabaseWrapper.FRIENDSHIPS_PATH);
    }

    private static CompletableFuture<List<String>> getUidsFromCollection(User user, String collectionName) {
        return database.getDocument(collectionName, user.getUid()).thenApply(stringObjectMap -> new ArrayList<>(stringObjectMap.keySet()));
    }
}