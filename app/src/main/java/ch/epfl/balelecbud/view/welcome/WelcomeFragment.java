package ch.epfl.balelecbud.view.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.HelpPage;

import static ch.epfl.balelecbud.utility.json.JsonResourceReader.getHelpPageCollection;

public final class WelcomeFragment extends Fragment {

    public static final String TAG = WelcomeFragment.class.getSimpleName();

    private ViewPager welcomeViewPager;
    private WelcomePagerAdapter welcomePagerAdapter;
    private TabLayout tabIndicator;
    private Bundle state;

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

        tabIndicator = getActivity().findViewById(R.id.welcome_tab_indicator);

        List<HelpPage> pages = getHelpPageCollection(R.raw.help_page);

        welcomeViewPager = getActivity().findViewById(R.id.welcome_view_pager);
        welcomePagerAdapter = new WelcomePagerAdapter(BalelecbudApplication.getAppContext(), pages);
        welcomeViewPager.setAdapter(welcomePagerAdapter);

        tabIndicator.setupWithViewPager(welcomeViewPager);

        Log.d(TAG, "onStart: trying to restore last state of the welcome screen, state " + state);
        SharedPreferences preferences = BalelecbudApplication
                .getAppContext().getSharedPreferences(WelcomeFragment.TAG, Context.MODE_PRIVATE);
        if(isSavedState(preferences) && state != null) {
            welcomeViewPager.onRestoreInstanceState(state.getParcelable(TAG));
            Log.d(TAG, "onStart: restored saved state successfully --> " + state);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: trying to save last state of the welcome screen");
        state = new Bundle();
        state.putParcelable(TAG, welcomeViewPager.onSaveInstanceState());
        SharedPreferences preferences = BalelecbudApplication
                .getAppContext().getSharedPreferences(WelcomeFragment.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        storeState(editor, state);
        editor.apply();
        Log.d(TAG, "onPause: saved state successfully --> " + state);
        Log.d(TAG, "onPause: isSavedState: " + isSavedState(preferences));
    }

    public static String restoreState(SharedPreferences preferences) {
        Log.d(TAG, "Restoring state");
        return isSavedState(preferences) ? preferences.getString(TAG, null) : null;
    }

    public static boolean isSavedState(SharedPreferences preferences) {
        return preferences.contains(TAG);
    }

    static void storeState(SharedPreferences.Editor editor, Bundle state) {
        Log.d(TAG, "Storing state: " + state);
        if (state != null) {
            Gson gson = new Gson();
            String serializedState = gson.toJson(state);
            editor.putString(TAG, serializedState);
        }
    }

    public void setState(Bundle state) {
        Log.d(TAG, "Setting state: " + state);
        this.state = state;
    }
}