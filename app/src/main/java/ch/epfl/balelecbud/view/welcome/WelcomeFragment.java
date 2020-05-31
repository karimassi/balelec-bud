package ch.epfl.balelecbud.view.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    public static final String TAG = WelcomeFragment.class.getSimpleName();

    private ViewPager welcomeViewPager;
    private WelcomePagerAdapter welcomePagerAdapter;
    private TabLayout pageIndicator;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        pageIndicator = getActivity().findViewById(R.id.welcome_tab_indicator);

        List<HelpPage> pages = getHelpPageCollection(R.raw.help_page);

        welcomeViewPager = getActivity().findViewById(R.id.welcome_view_pager);
        welcomePagerAdapter = new WelcomePagerAdapter(BalelecbudApplication.getAppContext(), pages);
        welcomeViewPager.setAdapter(welcomePagerAdapter);

        pageIndicator.setupWithViewPager(welcomeViewPager);

        SharedPreferences preferences = BalelecbudApplication
                .getAppContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);

        if(isPageNumberSaved(preferences)) {
            int pageNumber = preferences.getInt(TAG, -1);
            if(pageNumber == -1) {
                Log.d(TAG, "onStart: Failed to restore page");
            }
            else {
                welcomeViewPager.setCurrentItem(pageNumber);
                //pageIndicator.getTabAt(pageNumber);
                Log.d(TAG, "onStart: restored page number successfully --> " + pageNumber);
            }
            preferences.edit().clear().apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: trying to save last page of the welcome screen");
        int page = pageIndicator.getSelectedTabPosition();
        SharedPreferences preferences = BalelecbudApplication
                .getAppContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        preferences.edit().putInt(TAG, page).apply();
        Log.d(TAG, "onPause: saved page number successfully --> " + page);
        Log.d(TAG, "onPause: isPageNumberSaved: " + isPageNumberSaved(preferences));
    }

    private boolean isPageNumberSaved(SharedPreferences preferences) {
        return preferences.contains(TAG);
    }
}