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

public class SocialActivity extends BasicActivity {

    private SocialAdapter fragmentAdapter;
    ViewPager2 viewPager;

    private List<String> tabTitleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        tabTitleList = new ArrayList<>(Arrays.asList(getString(R.string.tab_friends), getString(R.string.tab_requests)));

        setupFragmentAdapter();
        viewPager = findViewById(R.id.view_pager_social);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabs = findViewById(R.id.tabs_social);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(tabTitleList.get(position))).attach();

        FloatingActionButton fabAddFriends = findViewById(R.id.fab_add_friends);
        fabAddFriends.setOnClickListener(v -> {
            AddFriendFragment dialog =AddFriendFragment.newInstance(getAuthenticator().getCurrentUser());
            dialog.show(getSupportFragmentManager(), getString(R.string.add_friend_title));
        });

    }

    private void setupFragmentAdapter() {
        fragmentAdapter = new SocialAdapter(getSupportFragmentManager(), getLifecycle());
        fragmentAdapter.addFragment(FriendsFragment.newInstance(getAuthenticator().getCurrentUser()));
        fragmentAdapter.addFragment(FriendRequestsFragment.newInstance(getAuthenticator().getCurrentUser()));
    }
}
