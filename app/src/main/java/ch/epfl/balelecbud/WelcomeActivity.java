package ch.epfl.balelecbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });
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

    /** Called when the user clicks the Map button */
    public void openMapActivity () {
        Intent intent = new Intent(this, MapViewActivity.class);
        startActivity(intent);
    }
}
