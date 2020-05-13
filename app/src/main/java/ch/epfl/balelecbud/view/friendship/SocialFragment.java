package ch.epfl.balelecbud.view.friendship;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;

public class SocialFragment extends Fragment {
    private SocialAdapter fragmentAdapter;

    private List<String> tabTitleList;

    private FragmentActivity activity;
    private View view;

    public static SocialFragment newInstance() {
        return (new SocialFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = getActivity();
        view = getView();

        tabTitleList = new ArrayList<>(Arrays.asList(getString(R.string.tab_friends), getString(R.string.tab_received_requests), getString(R.string.tab_sent_requests)));

        setupFragmentAdapter();
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_social);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabs = view.findViewById(R.id.tabs_social);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(tabTitleList.get(position))).attach();

        FloatingActionButton fabAddFriends = view.findViewById(R.id.fab_add_friends);
        fabAddFriends.setOnClickListener(v -> {
            AddFriendFragment dialog = AddFriendFragment.newInstance(getAppAuthenticator().getCurrentUser());
            dialog.show(activity.getSupportFragmentManager(), getString(R.string.add_friend_title));
        });
    }

    private void setupFragmentAdapter() {
        final User currentUser = getAppAuthenticator().getCurrentUser();
        fragmentAdapter = new SocialAdapter(activity.getSupportFragmentManager(), getLifecycle());
        fragmentAdapter.addFragment(FriendFragment.newInstance(currentUser));
        fragmentAdapter.addFragment(ReceivedFriendRequestsFragment.newInstance(currentUser));
        fragmentAdapter.addFragment(SentRequestFragment.newInstance(currentUser));
    }

}
