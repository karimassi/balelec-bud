package ch.epfl.balelecbud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends BasicActivity {
    private Class<? extends Activity> activityClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choseActivity();

    }

    private void choseActivity() {
        if (getAuthenticator().getCurrentUser() == null) {
            activityClass = LoginUserActivity.class;
        } else {
            activityClass = WelcomeActivity.class;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
