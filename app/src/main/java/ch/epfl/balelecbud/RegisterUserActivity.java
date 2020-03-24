package ch.epfl.balelecbud;

import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.function.BiConsumer;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.Callback;

public class RegisterUserActivity extends BasicActivity {

    private EditText emailField;
    private EditText passwordField;
    private EditText repeatPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        emailField = findViewById(R.id.editTextEmailRegister);
        passwordField = findViewById(R.id.editTextPasswordRegister);
        repeatPasswordField = findViewById(R.id.editTextRepeatPasswordRegister);
    }

    private void register(String email, String password) {
        if (!validateEntry()) {
            return;
        }
        getAuthenticator().createAccount(email, password).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void aVoid, Throwable throwable) {
                if (throwable != null) {
                    Toast.makeText(RegisterUserActivity.this, throwable.getCause().getLocalizedMessage() ,Toast.LENGTH_SHORT).show();
                } else {
                    onAuthComplete();
                }
            }
        });

    }

    private boolean validateEntry() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.require_email));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError(getString(R.string.invalid_email));
            valid = false;
        }

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

        if (!passwordRepeated.equals(password)) {
            passwordField.setError(getString(R.string.mismatch_password));
            repeatPasswordField.setError(getString(R.string.mismatch_password));
            valid = false;
        }

        return valid;
    }

    private void onAuthComplete() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.buttonRegister) {
            register(emailField.getText().toString(), passwordField.getText().toString());
        }
        if (view.getId() == R.id.buttonRegisterToLogin) {
            Intent intent = new Intent(RegisterUserActivity.this, LoginUserActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
