package ch.epfl.balelecbud.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Class<? extends Activity> activityClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            activityClass = LoginUserActivity.class;
        } else {
            activityClass = WelcomeActivity.class;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();

    }
}
