package ch.epfl.balelecbud.util.friendship;

import android.app.DownloadManager;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

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

    public static void addFriend(User friend, Callback<FriendRequest> callback) {
        FriendRequest request = new FriendRequest(auth.getCurrentUser().getUserToken(), friend.getUserToken(), 0);
        database.storeDocument(DatabaseWrapper.FRIEND_REQUESTS, request, callback);
    }

    public static void removeFriend() {
    }

    public static void acceptRequest(FriendRequest request, Callback<String> callback) {
        database.updateArrayElements(DatabaseWrapper.FRIENDSHIPS, auth.getCurrentUser().getUserToken(), "friends",request.getRecipientId());
        database.updateArrayElements(DatabaseWrapper.FRIENDSHIPS, request.getRecipientId(), "friends", auth.getCurrentUser().getUserToken());
        // TODO: delete the request
    }

    public static void rejectRequest() {

    }

}