package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;
import static ch.epfl.balelecbud.util.StringUtils.isEmailValid;

public class RegisterUserActivity extends AppCompatActivity {
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText repeatPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        nameField = findViewById(R.id.editTextNameRegister);
        emailField = findViewById(R.id.editTextEmailRegister);
        passwordField = findViewById(R.id.editTextPasswordRegister);
        repeatPasswordField = findViewById(R.id.editTextRepeatPasswordRegister);
    }

    private void register(String name, String email, String password) {
        if (!validateEntry())
            return;

        getAppAuthenticator().createAccount(name, email, password).whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
                Toast.makeText(
                        RegisterUserActivity.this,
                        throwable.getCause().getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            } else {
                onAuthComplete();
            }
        });

    }

    private boolean validateEntry() {
        boolean valid = true;
        if (!isNameValid())
            valid = false;

        if (!isEmailValid(this, emailField))
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

        if (!passwordRepeated.equals(password)) {
            passwordField.setError(getString(R.string.mismatch_password));
            repeatPasswordField.setError(getString(R.string.mismatch_password));
            valid = false;
        }
        return valid;
    }

    private boolean isNameValid() {
        String name = nameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameField.setError(getString(R.string.require_name));
           return false;
        }
        return true;
    }

    private void onAuthComplete() {
        getAppDatabaseWrapper()
                .getCustomDocument(DatabaseWrapper.USERS_PATH, getAppAuthenticator().getCurrentUid(), User.class)
                .whenComplete((user, throwable) -> {
                    if (throwable != null) {
                        Toast.makeText(
                                RegisterUserActivity.this,
                                throwable.getCause().getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        getAppAuthenticator().setCurrentUser(user);
                        Intent intent = new Intent(RegisterUserActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    public void onClick(View view) {
        if (view.getId() == R.id.buttonRegister) {
            register(
                    nameField.getText().toString(),
                    emailField.getText().toString(),
                    passwordField.getText().toString()
            );
        }
        if (view.getId() == R.id.buttonRegisterToLogin) {
            Intent intent = new Intent(RegisterUserActivity.this, LoginUserActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
