package ch.epfl.balelecbud.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.balelecbud.Authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class AuthenticationActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        emailField = findViewById(R.id.editTextEmailLogin);
        passwordField = findViewById(R.id.editTextPasswordLogin);

    }

    private void login(String email, String password) {
        if (!validateEntry()) {
            return;
        }

        FirebaseAuthenticator.getInstance().signIn(email, password, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    onAuthComplete();
                }
                else {
                    Toast.makeText(AuthenticationActivity.this, getString(R.string.login_failed),
                            Toast.LENGTH_SHORT).show();
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
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
        Intent intent = new Intent(this, MainActivity.class);
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
            finish();
        }
    }

}