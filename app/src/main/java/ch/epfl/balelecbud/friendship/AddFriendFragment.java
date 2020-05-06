package ch.epfl.balelecbud.friendship;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.ScheduleFragment;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.Database;

public class AddFriendFragment extends DialogFragment {

    private EditText editTextAddFriend;
    private static final String TAG = AddFriendFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_friend, null);
        Log.d(TAG, "onCreateDialog: view inflated");
        editTextAddFriend = view.findViewById(R.id.edit_text_email_add_friend);

        builder.setView(view)
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
                .setNegativeButton(R.string.add_friend_cancel,
                        (dialog, id) -> AddFriendFragment.this.getDialog().cancel());
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
