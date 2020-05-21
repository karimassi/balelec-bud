package ch.epfl.balelecbud.view.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.HelpPage;

import static ch.epfl.balelecbud.utility.json.JsonResourceReader.getHelpPageCollection;

public final class WelcomeFragment extends Fragment {

    private static final String TAG = WelcomeFragment.class.getSimpleName();

    private ViewPager welcomeViewPager;
    private WelcomePagerAdapter welcomePagerAdapter;
    private TabLayout tabIndicator;

    public static WelcomeFragment newInstance() {
        return (new WelcomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        tabIndicator = getActivity().findViewById(R.id.welcome_tab_indicator);

        List<HelpPage> pages = getHelpPageCollection(R.raw.help_page);

        welcomeViewPager = getActivity().findViewById(R.id.welcome_view_pager);
        welcomePagerAdapter = new WelcomePagerAdapter(BalelecbudApplication.getAppContext(), pages);
        welcomeViewPager.setAdapter(welcomePagerAdapter);

        tabIndicator.setupWithViewPager(welcomeViewPager);
    }
}