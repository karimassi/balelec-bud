package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import ch.epfl.balelecbud.emergency.EmergencyInfoData;
import ch.epfl.balelecbud.emergency.EmergencyInfoHolder;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

public class EmergencyInfoActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_info);

        configureToolBar(R.id.emergency_info_activity_toolbar);
        configureDrawerLayout(R.id.emergency_info_activity_drawer_layout);
        configureNavigationView(R.id.emergency_info_activity_nav_view);

        RecyclerView recyclerView = findViewById(R.id.emergencyInfoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        RecyclerViewData<EmergencyInfo, EmergencyInfoHolder> data = new EmergencyInfoData();
        RefreshableRecyclerViewAdapter<EmergencyInfo, EmergencyInfoHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(EmergencyInfoHolder::new, data, R.layout.item_emergencyinfo);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout_emergency_info);
        adapter.setOnRefreshListener(refreshLayout);
    }
}
