package ch.epfl.balelecbud.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.balelecbud.Authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

    private void signOut() {
        FirebaseAuthenticator.getInstance().signOut();
    }


    public void onClick(View view) {
        if (view.getId() == R.id.buttonSignOut) {
            signOut();
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
