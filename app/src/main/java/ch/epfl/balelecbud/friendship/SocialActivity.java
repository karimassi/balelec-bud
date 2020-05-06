package ch.epfl.balelecbud.friendship;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.balelecbud.BasicActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.models.User;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

public class SocialActivity extends BasicActivity {
    private SocialAdapter fragmentAdapter;

    private List<String> tabTitleList;

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
    }

    private void setupFragmentAdapter() {
        final User currentUser = getAppAuthenticator().getCurrentUser();
        fragmentAdapter = new SocialAdapter(getSupportFragmentManager(), getLifecycle());
        fragmentAdapter.addFragment(FriendsFragment.newInstance(currentUser));
        fragmentAdapter.addFragment(ReceivedFriendRequestsFragment.newInstance(currentUser));
        fragmentAdapter.addFragment(SentRequestFragment.newInstance(currentUser));
    }

}
