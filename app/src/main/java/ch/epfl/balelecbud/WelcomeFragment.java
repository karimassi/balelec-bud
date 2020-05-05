package ch.epfl.balelecbud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ch.epfl.balelecbud.cloudMessaging.TokenUtil;

public class WelcomeFragment extends Fragment {

    public static WelcomeFragment newInstance() {
        return (new WelcomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TokenUtil.storeToken();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}