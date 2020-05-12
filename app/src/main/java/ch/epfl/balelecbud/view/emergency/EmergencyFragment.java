package ch.epfl.balelecbud.view.emergency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import ch.epfl.balelecbud.R;

public class EmergencyFragment extends Fragment {

    private Button mShowEmergencyDialog;

    public static EmergencyFragment newInstance() {
        return (new EmergencyFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_emergency, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        mShowEmergencyDialog = getView().findViewById(R.id.buttonAskForHelp);
        InitiateViewWithValues();
    }

    private void InitiateViewWithValues() {
        mShowEmergencyDialog.setOnClickListener(v -> {
            SubmitEmergencyFragment dialog = SubmitEmergencyFragment.newInstance();
            dialog.show(getActivity().getSupportFragmentManager(), getString(R.string.declare_an_emergency));
        });
    }
}
