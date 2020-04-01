package ch.epfl.balelecbud;

import android.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.models.emergency.EmergencyType;


public class EmergencyActivity extends BasicActivity {

    private Button mShowEmergencyDialog;


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
                final EditText mEmergencyMessage = (EditText) mView.findViewById(R.id.textEmergencyMessage);
                final Spinner mEmergencyCategory = (Spinner) mView.findViewById(R.id.spinnerEmergencyCategories);
                Button mEmergencySubmit = (Button) mView.findViewById(R.id.buttonEmergencySubmit);
                mEmergencyCategory.setAdapter(new ArrayAdapter<EmergencyType>(EmergencyActivity.this, android.R.layout.simple_spinner_item, EmergencyType.values()));

                mEmergencySubmit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(!mEmergencyMessage.getText().toString().isEmpty() && !mEmergencyCategory.getSelectedItem().toString().isEmpty()){

                            // GET CURRENT USER AND TIMESTAMP
                            // SUBMIT TO DB HERE

                            Toast.makeText(EmergencyActivity.this,

                                    R.string.emergency_sent_message,
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(EmergencyActivity.this,
                                    R.string.emergency_not_sent_message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();



            }
        });
    }
}
