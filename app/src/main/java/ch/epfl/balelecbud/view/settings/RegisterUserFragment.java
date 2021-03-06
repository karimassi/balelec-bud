package ch.epfl.balelecbud.view.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.utility.StringUtils;
import ch.epfl.balelecbud.utility.cloudMessaging.TokenUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;
import ch.epfl.balelecbud.view.CustomDialogFragment;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.utility.StringUtils.isEmailValid;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;

public final class RegisterUserFragment extends CustomDialogFragment {
    public static final String TAG = RegisterUserFragment.class.getSimpleName();
    private final SettingsFragment settingsFragment;
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText repeatPasswordField;
    private TextWatcher watcher = StringUtils.getTextWatcher(() ->
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(validateEntry()));

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_register_user, null);

        setUpFields(view);

        builder.setView(view).setCustomTitle(getDialogCustomTitle())
                .setPositiveButton(R.string.action_register, (dialog, id) ->
                        register(
                                nameField.getText().toString(),
                                emailField.getText().toString(),
                                passwordField.getText().toString()))
                .setNeutralButton(R.string.action_existing_account, (dialog, which) -> {
                    dismiss();
                    DialogFragment registerDialog = LoginUserFragment.newInstance(settingsFragment);
                    registerDialog.show(getActivity().getSupportFragmentManager(), LoginUserFragment.TAG);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        view.post(() -> {
            AlertDialog dialog = ((AlertDialog) getDialog());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        });
        validateEntry();
        return builder.create();
    }

    private void setUpFields(View view) {
        nameField = view.findViewById(R.id.editTextNameRegister);
        emailField = view.findViewById(R.id.editTextEmailRegister);
        passwordField = view.findViewById(R.id.editTextPasswordRegister);
        repeatPasswordField = view.findViewById(R.id.editTextRepeatPasswordRegister);

        nameField.addTextChangedListener(watcher);
        emailField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);
        repeatPasswordField.addTextChangedListener(watcher);
    }

    private RegisterUserFragment(SettingsFragment settingsFragment) {
        super(R.string.register);
        this.settingsFragment = settingsFragment;
    }

    private void register(String name, String email, String password) {
        settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.CONNECTING);
        getAppAuthenticator().createAccount(name, email, password).whenComplete((aVoid, throwable) -> {
            Log.d(TAG, "whenComplete() called with: aVoid = [" + aVoid + "], throwable = [" + throwable + "]");
            if (throwable != null) {
                Snackbar.make(settingsFragment.getView(), R.string.register_failed, Snackbar.LENGTH_LONG).show();
                settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.SIGNED_OUT);
            } else {
                onAuthComplete();
            }
        });

    }

    public static RegisterUserFragment newInstance(SettingsFragment settingsFragment) {
        return new RegisterUserFragment(settingsFragment);
    }

    private boolean validateEntry() {
        // Not using boolean operator because we want to avoid short circuiting and ensure that
        // all failing fields are displayed at once to the user
        boolean valid = true;
        if (!isNameValid())
            valid = false;

        if (!isEmailValid(getContext(), emailField))
            valid = false;

        if (!isPasswordsValid())
            valid = false;

        return valid;
    }

    private boolean isPasswordsValid() {
        boolean valid = true;
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.require_password));
            valid = false;
        } else if (password.length() < 6) {
            passwordField.setError(getString(R.string.invalid_password));
            valid = false;
        }

        String passwordRepeated = repeatPasswordField.getText().toString();
        if (TextUtils.isEmpty(passwordRepeated)) {
            repeatPasswordField.setError(getString(R.string.require_password_repeated));
            valid = false;
        }

        if (valid && !passwordRepeated.equals(password)) {
            passwordField.setError(getString(R.string.mismatch_password));
            repeatPasswordField.setError(getString(R.string.mismatch_password));
            valid = false;
        }

        if(valid){
            passwordField.setError(null);
            repeatPasswordField.setError(null);
        }

        return valid;
    }

    private boolean isNameValid() {
        String name = nameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameField.setError(getString(R.string.require_name));
            return false;
        }else{
            nameField.setError(null);
            return true;
        }
    }

    private void onAuthComplete() {
        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, MyWhereClause.Operator.EQUAL, getAppAuthenticator().getCurrentUid()));
        getAppDatabase().query(query, User.class)
                .whenComplete((users, throwable) -> {
                    Log.d(TAG, "onAuthComplete() called with users = [ " + users + " ], throwable = [ " + throwable + " ]");
                    if (throwable != null) {
                        Snackbar.make(settingsFragment.getView(), R.string.register_failed, Snackbar.LENGTH_LONG).show();
                        settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.SIGNED_OUT);
                    } else {
                        getAppAuthenticator().setCurrentUser(users.getList().get(0));
                        settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.SIGNED_IN);
                        TokenUtils.storeToken();
                    }
                });
    }
}
