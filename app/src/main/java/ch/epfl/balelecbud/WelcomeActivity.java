package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.annotation.NonNull;

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.location.LocationUtil.Action;

public class WelcomeActivity extends BasicActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Switch locationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        this.configureToolBar(R.id.root_activity_toolbar);
        this.configureDrawerLayout(R.id.root_activity_drawer_layout);
        this.configureNavigationView(R.id.root_activity_nav_view);
    }

}