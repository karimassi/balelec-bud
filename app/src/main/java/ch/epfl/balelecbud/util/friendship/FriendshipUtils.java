package ch.epfl.balelecbud.util.friendship;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.models.FriendRequest;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;
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
        FriendRequest request = new FriendRequest(auth.getCurrentUser().getUid(), friend.getUid());
        database.storeDocumentWithID(DatabaseWrapper.FRIEND_REQUESTS, request.getRecipientId(), request);
    }

    public static void removeFriend(User friend) {
        Map<String,Object> updates = new HashMap<>();
        updates.put(friend.getUid(), FieldValue.delete());
        database.updateDocument(DatabaseWrapper.FRIENDSHIPS, auth.getCurrentUser().getUid(), updates);

        updates = new HashMap<>();
        updates.put(auth.getCurrentUser().getUid(), FieldValue.delete());
        database.updateDocument(DatabaseWrapper.FRIENDSHIPS, friend.getUid(), updates);
    }

    public static void acceptRequest(FriendRequest request) {
        Map<String,Boolean> toStore= new HashMap<>();
        toStore.put(request.getSenderId(), true);
        database.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS, request.getRecipientId(), toStore);

        toStore = new HashMap<>();
        toStore.put(request.getRecipientId(), true);
        database.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS, request.getSenderId(), toStore);

        deleteRequest(request);
    }

    public static void deleteRequest(FriendRequest request) {
        database.deleteDocument(DatabaseWrapper.FRIEND_REQUESTS, request.getRecipientId());
    }

}