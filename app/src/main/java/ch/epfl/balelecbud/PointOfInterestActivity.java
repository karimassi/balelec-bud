package ch.epfl.balelecbud;

import android.media.session.MediaSession;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.cloudMessaging.Message;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestData;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestHolder;
import ch.epfl.balelecbud.util.TokenUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.views.RecyclerViewData;
import ch.epfl.balelecbud.util.views.RefreshableRecyclerViewAdapter;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

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
        RecyclerViewData<PointOfInterest, PointOfInterestHolder> data = new PointOfInterestData();
        RefreshableRecyclerViewAdapter<PointOfInterest, PointOfInterestHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                PointOfInterestHolder::new, data, R.layout.item_point_of_interest);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout_point_of_interest);
        adapter.setOnRefreshListener(refreshLayout);


        // TODO: this is just for testing, erase after.
        Message m = new Message("", "Hello", "Hi");
        try {
            Log.d("CloudMessagingService", "before sending message in POI");
            m.sendMessage("coLwjxkbHrc:APA91bGdzyzSkaKcm9LLQngAP7q_zE5xvPjUT5tGcoapphSrSymlRTWpAo9rvI0Zff9TSXRlKglIbF0Wc9eAfKvUC0ZNufalhyteIMgoRHnJYT_mqRWQ-4ahJ35dxmEGGIIbVgkrXzaZ");
            Log.d("CloudMessagingService", "after sending message in POI");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.d("CloudMessagingService", "problem sending lol");
        }
    }

}
