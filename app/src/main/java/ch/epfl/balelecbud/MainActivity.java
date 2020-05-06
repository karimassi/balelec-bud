package ch.epfl.balelecbud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choseActivity();
    }

    private void choseActivity() {
        Class<? extends Activity> activityClass;
        if (getAppAuthenticator().getCurrentUser() == null) {
            activityClass = LoginUserActivity.class;
        } else {
            activityClass = RootActivity.class;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
