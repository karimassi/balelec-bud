package ch.epfl.balelecbud;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends BasicActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
