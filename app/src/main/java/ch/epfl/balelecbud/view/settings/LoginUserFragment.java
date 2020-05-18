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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.StringUtils;
import ch.epfl.balelecbud.utility.cloudMessaging.TokenUtils;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.utility.StringUtils.isEmailValid;

public final class LoginUserFragment extends DialogFragment {
    public static final String TAG = LoginUserFragment.class.getSimpleName();
    private final SettingsFragment settingsFragment;
    private EditText emailField;
    private EditText passwordField;
    private TextWatcher watcher = StringUtils.getTextWatcher(() ->
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(validateEntry()));

    private LoginUserFragment(SettingsFragment settingsFragment) {
        this.settingsFragment = settingsFragment;
    }

    static LoginUserFragment newInstance(SettingsFragment settingsFragment) {
        return new LoginUserFragment(settingsFragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sign_in, null);

        emailField = view.findViewById(R.id.editTextEmailLogin);
        passwordField = view.findViewById(R.id.editTextPasswordLogin);

        emailField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);

        builder.setView(view).setTitle(R.string.sign_in)
                .setPositiveButton(R.string.action_sign_in, (dialog, id) ->
                        login(emailField.getText().toString(), passwordField.getText().toString()))
                .setNeutralButton(R.string.action_no_account, (dialog, which) -> {
                    dismiss();
                    DialogFragment registerDialog = RegisterUserFragment.newInstance(settingsFragment);
                    registerDialog.show(getActivity().getSupportFragmentManager(), RegisterUserFragment.TAG);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        view.post(() -> {
            AlertDialog dialog = ((AlertDialog) getDialog());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        });
        return builder.create();
    }

    private void login(final String email, String password) {
        if (!validateEntry()) {
            return;
        }
        settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.CONNECTING);
        getAppAuthenticator().signIn(email, password).whenComplete((user, throwable) -> {
            if (user != null) {
                getAppAuthenticator().setCurrentUser(user);
                Log.d(TAG, "login: successful" + user);
                onAuthComplete();
            } else {
                Log.w(TAG, "login: fail", throwable);
                Toast.makeText(getContext(), getString(R.string.sign_in_failed), Toast.LENGTH_LONG).show();
                settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.SIGNED_OUT);
            }
        });
    }

    private boolean validateEntry() {
        boolean valid = true;

        if (!isEmailValid(getContext(), emailField))
            valid = false;

        passwordField.setError(null);
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.require_password));
            valid = false;
        }
        return valid;
    }

    private void onAuthComplete() {
        TokenUtils.storeToken();
        settingsFragment.updateLoginStatus(SettingsFragment.ConnectionStatus.SIGNED_IN);
    }
}