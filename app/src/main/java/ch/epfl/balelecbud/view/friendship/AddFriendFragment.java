package ch.epfl.balelecbud.view.friendship;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.StringUtils;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

public final class AddFriendFragment extends DialogFragment {

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
                .setCustomTitle(getDialogCustomTitle())
                .setPositiveButton(R.string.add_friend_request, (dialog, id) -> {
                    if (validateEmail()) {
                        FriendshipUtils.getUserFromEmail(editTextAddFriend.getText().toString(), InformationSource.REMOTE_ONLY)
                                .whenComplete((user, throwable) -> FriendshipUtils.requestFriend(user));
                        Snackbar.make(getTargetFragment().getView(),
                                getString(R.string.add_friend_request_sent) + editTextAddFriend.getText(), LENGTH_SHORT ).show();
                    }

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        view.post(() -> {
            AlertDialog dialog = ((AlertDialog) getDialog());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        });

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getTargetFragment().onResume();
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
            editTextAddFriend.setError(getString(R.string.add_own_as_friend));
            return false;
        }
        return true;
    }

    private TextView getDialogCustomTitle() {
        TextView title = new TextView(getContext());
        title.setText(R.string.add_friend_title);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setPaddingRelative(0, 10, 0, 10);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        return title;
    }
}
