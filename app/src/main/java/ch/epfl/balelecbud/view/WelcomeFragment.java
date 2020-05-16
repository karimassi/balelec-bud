package ch.epfl.balelecbud.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ch.epfl.balelecbud.R;

public class WelcomeFragment extends Fragment {

    private static final String TAG = WelcomeFragment.class.getSimpleName();

    public static WelcomeFragment newInstance() {
        return (new WelcomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}