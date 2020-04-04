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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import ch.epfl.balelecbud.festivalInformation.FestivalInformationAdapter;
import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.festivalInformation.FestivalInformationData;
import ch.epfl.balelecbud.festivalInformation.FestivalInformationHolder;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class FestivalInformationActivity extends BasicActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter festivalInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_info);

        recyclerView = findViewById(R.id.festivalInfoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        RecyclerViewData<FestivalInformation, FestivalInformationHolder> data = new FestivalInformationData();
        RefreshableRecyclerViewAdapter<FestivalInformation, FestivalInformationHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(FestivalInformationHolder::new, data, R.layout.item_festival_info);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout_festival_info);
        adapter.setOnRefreshListener(refreshLayout);
        
        configureToolBar(R.id.festival_info_activity_toolbar);
        configureDrawerLayout(R.id.festival_info_activity_drawer_layout);
        configureNavigationView(R.id.festival_info_activity_nav_view);
    }

}