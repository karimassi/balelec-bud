package ch.epfl.balelecbud.view.friendship;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.StringUtils;
import ch.epfl.balelecbud.utility.database.Database;

public class AddFriendFragment extends DialogFragment {

    private EditText editTextAddFriend;
    private static final String TAG = AddFriendFragment.class.getSimpleName();
    private TextWatcher watcher = StringUtils.getTextWatcher(() ->
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(validateEmail()));

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_friend, null);
        Log.d(TAG, "onCreateDialog: view inflated");
        editTextAddFriend = view.findViewById(R.id.edit_text_email_add_friend);
        editTextAddFriend.addTextChangedListener(watcher);

        builder.setView(view)
                .setTitle(R.string.add_friend_title)
                .setPositiveButton(R.string.add_friend_request, (dialog, id) -> {
                    if (validateEmail()) {
                        FriendshipUtils.getUserFromEmail(editTextAddFriend.getText().toString(), Database.Source.REMOTE)
                                .whenComplete((user, throwable) -> FriendshipUtils.addFriend(user));
                        Toast.makeText(
                                getContext(),
                                getString(R.string.add_friend_request_sent) + editTextAddFriend.getText(),
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        view.post(() -> {
            AlertDialog dialog = ((AlertDialog) getDialog());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        });

        return builder.create();
    }

    static AddFriendFragment newInstance(User user) {
        AddFriendFragment f = new AddFriendFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        f.setArguments(args);
        return f;
    }

    private boolean validateEmail() {
        String email = editTextAddFriend.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextAddFriend.setError(getString(R.string.require_email));
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextAddFriend.setError(getString(R.string.invalid_email));
            return false;
        } else if (email.equals(((User)getArguments().get("user")).getEmail())) {
            Toast.makeText(
                    getContext(),
                    R.string.add_own_as_friend,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
