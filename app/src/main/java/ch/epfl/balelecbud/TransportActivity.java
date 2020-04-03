package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.transport.TransportListFragment;
import ch.epfl.balelecbud.transport.objects.Transport;

public class TransportActivity extends BasicActivity implements TransportListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        configureToolBar(R.id.transport_activity_toolbar);
        configureDrawerLayout(R.id.transport_activity_drawer_layout);
        configureNavigationView(R.id.transport_activity_nav_view);
    }

    @Override
    public void onListFragmentInteraction(Transport item) {
        //do nothing for now
    }

}
