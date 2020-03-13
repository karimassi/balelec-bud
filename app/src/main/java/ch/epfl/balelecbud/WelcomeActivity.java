package ch.epfl.balelecbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


import ch.epfl.balelecbud.authentication.FirebaseAuthenticator;
import ch.epfl.balelecbud.schedule.ScheduleActivity;

public class WelcomeActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfoActivity();
            }
        });

        Button scheduleButton = findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScheduleActivity();
            }
        });

        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });

        Button transportButton = findViewById(R.id.transportButton);
        transportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransportActivity();
            }
        });

        final Button signoutButton = findViewById(R.id.buttonSignOut);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        Switch locationSwitch = findViewById(R.id.locationSwitch);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //should switch on the location
            }
        });
    }

    private void signOut() {
        getAuthenticator().signOut();
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }

    /** Called when the user clicks the Map button */
    public void openMapActivity () {
        Intent intent = new Intent(this, MapViewActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Schedule button */
    public void openScheduleActivity(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Schedule button */
    public void openInfoActivity(){
        //Intent intent = new Intent(this, ScheduleActivity.class);
        //startActivity(intent);
    }

    /** Called when the user clicks the Schedule button */
    public void openTransportActivity(){
        //Intent intent = new Intent(this, ScheduleActivity.class);
        //startActivity(intent);
    }
}
