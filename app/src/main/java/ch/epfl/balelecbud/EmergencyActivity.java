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


public class EmergencyActivity extends AppCompatActivity {

    private Button mShowEmergencyDialog;
    private static DatabaseWrapper database = FirestoreDatabaseWrapper.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        mShowEmergencyDialog = (Button) findViewById(R.id.buttonAskForHelp);
        mShowEmergencyDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder((EmergencyActivity.this));
                View mView = getLayoutInflater().inflate(R.layout.dialog_emergency,null);
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                final EditText mEmergencyMessage = (EditText) mView.findViewById(R.id.textEmergencyMessage);
                final Spinner mEmergencyCategory = (Spinner) mView.findViewById(R.id.spinnerEmergencyCategories);
                Button mEmergencySubmit = (Button) mView.findViewById(R.id.buttonEmergencySubmit);
                mEmergencyCategory.setAdapter(new ArrayAdapter<EmergencyType>(EmergencyActivity.this, android.R.layout.simple_spinner_item, EmergencyType.values()));
                mEmergencySubmit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String emergencyMessage = mEmergencyMessage.getText().toString();
                        EmergencyType emergencyType = EmergencyType.valueOf(mEmergencyCategory.getSelectedItem().toString().toUpperCase());
                        if(!emergencyMessage.isEmpty()){
                            String currentUserUid = getAppAuthenticator().getCurrentUser().getUid();
                            Timestamp currentTimestamp = Timestamp.now();
                            Emergency mEmergency = new Emergency(emergencyType, emergencyMessage,currentUserUid,currentTimestamp);
                            database.storeDocument(DatabaseWrapper.EMERGENCIES_PATH, mEmergency);
                            Toast.makeText(EmergencyActivity.this, R.string.emergency_sent_message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(EmergencyActivity.this, R.string.emergency_not_sent_message, Toast.LENGTH_SHORT).show();
                        }



                    }
                });

                dialog.show();
            }
        });
    }
}
