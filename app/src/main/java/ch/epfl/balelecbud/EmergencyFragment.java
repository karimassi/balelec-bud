package ch.epfl.balelecbud;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;

import ch.epfl.balelecbud.emergency.SubmitEmergencyFragment;
import ch.epfl.balelecbud.friendship.AddFriendFragment;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.models.emergency.EmergencyType;
import ch.epfl.balelecbud.util.database.Database;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

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
