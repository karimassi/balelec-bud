package ch.epfl.balelecbud.friendship;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.function.BiConsumer;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.models.User;

public class AddFriendFragment extends DialogFragment {

    EditText editTextAddFriend;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_friend, null);

        editTextAddFriend = view.findViewById(R.id.edit_text_email_add_friend);

        builder.setView(view)
                .setPositiveButton(R.string.add_friend_request, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (validateEmail()) {
                            FriendshipUtils.getUserFromEmail(editTextAddFriend.getText().toString()).whenComplete(new BiConsumer<User, Throwable>() {
                                @Override
                                public void accept(User user, Throwable throwable) {
                                    FriendshipUtils.addFriend(user);
                                }
                            });
                            Toast.makeText(getContext(), getString(R.string.add_friend_request_sent) + editTextAddFriend.getText(), Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton(R.string.add_friend_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddFriendFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private boolean validateEmail() {
        String email = editTextAddFriend.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextAddFriend.setError(getString(R.string.require_email));
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextAddFriend.setError(getString(R.string.invalid_email));
            return false;
        } else if (email.equals(FirebaseAuthenticator.getInstance().getCurrentUser().getEmail())) {
            Toast.makeText(getContext(), R.string.add_own_as_friend, Toast.LENGTH_SHORT).show();
            return false;
        } else
        return true;
    }
}
