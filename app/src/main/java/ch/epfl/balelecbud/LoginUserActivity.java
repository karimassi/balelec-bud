package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.function.BiConsumer;

import ch.epfl.balelecbud.models.User;

public class LoginUserActivity extends BasicActivity {

    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        emailField = findViewById(R.id.editTextEmailLogin);
        passwordField = findViewById(R.id.editTextPasswordLogin);

    }

    private void login(final String email, String password) {
        if (!validateEntry()) {
            return;
        }

        getAuthenticator().signIn(email, password).whenComplete(new BiConsumer<User, Throwable>() {
            @Override
            public void accept(User user, Throwable throwable) {
                if (user != null) {
                    getAuthenticator().setCurrentUser(user);
                    onAuthComplete();
                } else {
                    Toast.makeText(LoginUserActivity.this, throwable.getCause().getLocalizedMessage() ,Toast.LENGTH_SHORT).show();
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
        }
        return valid;
    }

    private void onAuthComplete() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.buttonLogin) {
            login(emailField.getText().toString(), passwordField.getText().toString());
        }
        if (view.getId() == R.id.buttonLoginToRegister) {
            Intent intent = new Intent(this, RegisterUserActivity.class);
            startActivity(intent);
        }
    }
}