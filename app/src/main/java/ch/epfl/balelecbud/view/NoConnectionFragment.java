package ch.epfl.balelecbud.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.epfl.balelecbud.R;

public final class NoConnectionFragment extends Fragment {
    private NoConnectionFragment() {}
    public static NoConnectionFragment newInstance() {
        return new NoConnectionFragment();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_no_connection, container, false);
    }
}
