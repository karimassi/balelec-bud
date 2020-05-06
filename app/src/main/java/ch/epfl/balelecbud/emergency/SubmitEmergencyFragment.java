package ch.epfl.balelecbud.emergency;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.friendship.AddFriendFragment;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.models.emergency.EmergencyType;
import ch.epfl.balelecbud.util.database.Database;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public class SubmitEmergencyFragment extends DialogFragment {

    private static final String TAG = SubmitEmergencyFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_emergency, null);
        Log.d(TAG, "onCreateDialog: view inflated");
        builder.setView(mView);
        AlertDialog dialog = builder.create();

        final EditText mEmergencyMessage = mView.findViewById(R.id.textEmergencyMessage);
        final Spinner mEmergencyCategory = mView.findViewById(R.id.spinnerEmergencyCategories);
        Button mEmergencySubmit = mView.findViewById(R.id.buttonEmergencySubmit);
        mEmergencyCategory.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, EmergencyType.values()));
        submitEmergency(dialog, mEmergencyMessage, mEmergencyCategory, mEmergencySubmit);

        return dialog;
    }


    private void submitEmergency(AlertDialog dialog, EditText mEmergencyMessage, Spinner mEmergencyCategory, Button mEmergencySubmit) {
        mEmergencySubmit.setOnClickListener(v1 -> {
            String emergencyMessage = mEmergencyMessage.getText().toString();
            EmergencyType emergencyType = EmergencyType.valueOf(mEmergencyCategory.getSelectedItem().toString().toUpperCase());
            if(!emergencyMessage.isEmpty()){
                String currentUserUid = getAppAuthenticator().getCurrentUser().getUid();
                Timestamp currentTimestamp = Timestamp.now();
                Emergency mEmergency = new Emergency(emergencyType, emergencyMessage,currentUserUid,currentTimestamp);
                getAppDatabase().storeDocument(Database.EMERGENCIES_PATH, mEmergency);
                Toast.makeText(getActivity(), R.string.emergency_sent_message, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }else{
                Toast.makeText(getActivity(), R.string.emergency_not_sent_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static SubmitEmergencyFragment newInstance() {
        SubmitEmergencyFragment f = new SubmitEmergencyFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

}
