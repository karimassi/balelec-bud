package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestData;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestHolder;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class PointOfInterestActivity extends BasicActivity {

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
        RecyclerViewData<PointOfInterest, PointOfInterestHolder> data = new PointOfInterestData(this);
        RefreshableRecyclerViewAdapter<PointOfInterest, PointOfInterestHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                PointOfInterestHolder::new, data, R.layout.item_point_of_interest);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout_point_of_interest);
        adapter.setOnRefreshListener(refreshLayout);
    }
}
