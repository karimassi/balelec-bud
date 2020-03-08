package ch.epfl.balelecbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import ch.epfl.balelecbud.Authentication.FirebaseAuthenticator;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    private void signOut() {
        FirebaseAuthenticator.getInstance().signOut();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.buttonSignOut) {
            signOut();
            Intent intent = new Intent(this, LoginUserActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
