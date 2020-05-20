package ch.epfl.balelecbud.view.settings;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.location.LocationUtils;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public final class DeleteAccountDialog extends DialogFragment {
    public static final String TAG = DeleteAccountDialog.class.getSimpleName();
    private final SettingsFragment settingsFragment;

    private DeleteAccountDialog(SettingsFragment settingsFragment) {
        this.settingsFragment = settingsFragment;
    }

    static DeleteAccountDialog newInstance(SettingsFragment settingsFragment) {
        return new DeleteAccountDialog(settingsFragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(R.string.delete_account_text)
                .setTitle(R.string.delete_account)
                .setPositiveButton(R.string.delete_account_yes, (dialog, id) -> deleteUser())
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        return builder.create();
    }

    private void deleteUser() {
        String uid = getAppAuthenticator().getCurrentUid();
        getAppDatabase().deleteDocumentWithID(Database.USERS_PATH, uid);
        getAppDatabase().deleteDocumentWithID(Database.LOCATIONS_PATH, uid);
        FriendshipUtils.getFriendsUids(getAppAuthenticator().getCurrentUser()).whenComplete(
                (strings, throwable) ->
                        FriendshipUtils.getUsersFromUids(strings, Database.Source.CACHE_FIRST)
                                .forEach(userCompletableFuture -> userCompletableFuture.whenComplete(
                                        (user, throwable1) -> FriendshipUtils.removeFriend(user))));
        getAppDatabase().deleteDocumentWithID(Database.SENT_REQUESTS_PATH, uid);
        getAppDatabase().deleteDocumentWithID(Database.FRIEND_REQUESTS_PATH, uid);
        getAppDatabase().deleteDocumentWithID(Database.FRIENDSHIPS_PATH, uid);
        getAppAuthenticator().deleteCurrentUser().whenComplete(
                (aVoid, throwable) -> onDeleteCompleted());
    }

    private void onDeleteCompleted() {
        if (LocationUtils.isLocationActive())
            LocationUtils.disableLocation();
        getAppAuthenticator().signOut();
        getAppAuthenticator().signInAnonymously();
        settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.SIGNED_OUT);
    }
}
