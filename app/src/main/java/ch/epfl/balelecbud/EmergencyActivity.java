package ch.epfl.balelecbud;

import android.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.models.emergency.EmergencyType;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;


public class EmergencyActivity extends AppCompatActivity {

    private Button mShowEmergencyDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        mShowEmergencyDialog = findViewById(R.id.buttonAskForHelp);
        mShowEmergencyDialog.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder((EmergencyActivity.this));
            View mView = getLayoutInflater().inflate(R.layout.dialog_emergency,null);
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            final EditText mEmergencyMessage = mView.findViewById(R.id.textEmergencyMessage);
            final Spinner mEmergencyCategory = mView.findViewById(R.id.spinnerEmergencyCategories);
            Button mEmergencySubmit = mView.findViewById(R.id.buttonEmergencySubmit);
            mEmergencyCategory.setAdapter(new ArrayAdapter<>(EmergencyActivity.this, android.R.layout.simple_spinner_item, EmergencyType.values()));
            mEmergencySubmit.setOnClickListener(v1 -> {
                String emergencyMessage = mEmergencyMessage.getText().toString();
                EmergencyType emergencyType = EmergencyType.valueOf(mEmergencyCategory.getSelectedItem().toString().toUpperCase());
                if(!emergencyMessage.isEmpty()){
                    String currentUserUid = getAppAuthenticator().getCurrentUser().getUid();
                    Timestamp currentTimestamp = Timestamp.now();
                    Emergency mEmergency = new Emergency(emergencyType, emergencyMessage,currentUserUid,currentTimestamp);
                    getAppDatabaseWrapper().storeDocument(DatabaseWrapper.EMERGENCIES_PATH, mEmergency);
                    Toast.makeText(EmergencyActivity.this, R.string.emergency_sent_message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    Toast.makeText(EmergencyActivity.this, R.string.emergency_not_sent_message, Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        });
    }
}
