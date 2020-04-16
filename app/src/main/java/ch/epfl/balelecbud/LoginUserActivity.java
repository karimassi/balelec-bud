package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.util.StringUtils.isEmailValid;

public class LoginUserActivity extends AppCompatActivity {
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

        getAppAuthenticator().signIn(email, password).whenComplete((user, throwable) -> {
            if (user != null) {
                getAppAuthenticator().setCurrentUser(user);
                onAuthComplete();
            } else {
                Toast.makeText(LoginUserActivity.this, throwable.getCause()
                        .getLocalizedMessage() ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEntry() {
        boolean valid = true;

        if (!isEmailValid(this, emailField))
            valid = false;

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.require_password));
            valid = false;
        }
        return valid;
    }

    private void onAuthComplete() {
        Intent intent = new Intent(this, RootActivity.class);
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