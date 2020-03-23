package ch.epfl.balelecbud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choseActivity();
    }

    private void choseActivity() {
        Class<? extends Activity> activityClass;
        if (getAuthenticator().getCurrentUser() == null) {
            activityClass = LoginUserActivity.class;
        } else {
            activityClass = WelcomeActivity.class;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
