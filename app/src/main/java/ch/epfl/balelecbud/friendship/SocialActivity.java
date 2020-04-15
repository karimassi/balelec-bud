package ch.epfl.balelecbud.friendship;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.balelecbud.BasicActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.cloudMessaging.CloudMessagingService;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.notifications.concertSoon.NotificationScheduler;
import ch.epfl.balelecbud.schedule.models.Slot;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

public class SocialActivity extends BasicActivity {
    private SocialAdapter fragmentAdapter;

    private List<String> tabTitleList;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        this.configureToolBar(R.id.social_activity_toolbar);
        this.configureDrawerLayout(R.id.social_activity_drawer_layout);
        this.configureNavigationView(R.id.social_activity_nav_view);

        tabTitleList = new ArrayList<>(Arrays.asList(getString(R.string.tab_friends), getString(R.string.tab_received_requests), getString(R.string.tab_sent_requests)));

        setupFragmentAdapter();
        ViewPager2 viewPager = findViewById(R.id.view_pager_social);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabs = findViewById(R.id.tabs_social);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(tabTitleList.get(position))).attach();

        FloatingActionButton fabAddFriends = findViewById(R.id.fab_add_friends);
        fabAddFriends.setOnClickListener(v -> {
            AddFriendFragment dialog = AddFriendFragment.newInstance(getAppAuthenticator().getCurrentUser());
            dialog.show(getSupportFragmentManager(), getString(R.string.add_friend_title));
        });

        //TODO: all modifs
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                String title = (String) bundle.get("title");

                new AlertDialog.Builder(context).setMessage(title).create().show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(CloudMessagingService.SEND_NOTIFICATION_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void setupFragmentAdapter() {
        final User currentUser = getAppAuthenticator().getCurrentUser();
        fragmentAdapter = new SocialAdapter(getSupportFragmentManager(), getLifecycle());
        fragmentAdapter.addFragment(FriendsFragment.newInstance(currentUser));
        fragmentAdapter.addFragment(ReceivedFriendRequestsFragment.newInstance(currentUser));
        fragmentAdapter.addFragment(SentRequestFragment.newInstance(currentUser));
    }

}
