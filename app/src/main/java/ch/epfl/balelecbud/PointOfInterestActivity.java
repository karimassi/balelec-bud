package ch.epfl.balelecbud;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestAdapter;

public class PointOfInterestActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_interest);

        configureToolBar(R.id.poi_activity_toolbar);
        configureDrawerLayout(R.id.poi_activity_drawer_layout);
        configureNavigationView(R.id.poi_activity_nav_view);

        RecyclerView recyclerView = findViewById(R.id.pointOfInterestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        RecyclerView.Adapter pointOfInterestAdapter = new PointOfInterestAdapter();
        recyclerView.setAdapter(pointOfInterestAdapter);
    }

}
