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

import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.models.emergency.EmergencyType;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;


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
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            View mView = getLayoutInflater().inflate(R.layout.dialog_emergency,null);
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            final EditText mEmergencyMessage = mView.findViewById(R.id.textEmergencyMessage);
            final Spinner mEmergencyCategory = mView.findViewById(R.id.spinnerEmergencyCategories);
            Button mEmergencySubmit = mView.findViewById(R.id.buttonEmergencySubmit);
            mEmergencyCategory.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, EmergencyType.values()));
            submitEmergency(dialog, mEmergencyMessage, mEmergencyCategory, mEmergencySubmit);

            dialog.show();
        });
    }

    private void submitEmergency(AlertDialog dialog, EditText mEmergencyMessage, Spinner mEmergencyCategory, Button mEmergencySubmit) {
        mEmergencySubmit.setOnClickListener(v1 -> {
            String emergencyMessage = mEmergencyMessage.getText().toString();
            EmergencyType emergencyType = EmergencyType.valueOf(mEmergencyCategory.getSelectedItem().toString().toUpperCase());
            if(!emergencyMessage.isEmpty()){
                String currentUserUid = getAppAuthenticator().getCurrentUser().getUid();
                Timestamp currentTimestamp = Timestamp.now();
                Emergency mEmergency = new Emergency(emergencyType, emergencyMessage,currentUserUid,currentTimestamp);
                getAppDatabaseWrapper().storeDocument(DatabaseWrapper.EMERGENCIES_PATH, mEmergency);
                Toast.makeText(getActivity(), R.string.emergency_sent_message, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }else{
                Toast.makeText(getActivity(), R.string.emergency_not_sent_message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
