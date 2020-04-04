package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.festivalInformation.FestivalInformationData;
import ch.epfl.balelecbud.festivalInformation.FestivalInformationHolder;
import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class FestivalInformationActivity extends BasicActivity {

    private RecyclerView recyclerView;

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