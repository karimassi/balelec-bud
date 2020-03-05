package ch.epfl.balelecbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ch.epfl.balelecbud.schedule.models.Slot;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "ch.epfl.bootcamp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        //Intent intent = new Intent(this, GreetingActivity.class);
        //EditText editText = (EditText) findViewById(R.id.mainTextEdit);
        //String message = "Hello from my unit test!";
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("dev");
        Slot slot1 = new Slot("Moi", "12-13", "La grande scene");
        mDatabase.child("slots").child("slot1").setValue(slot1);
    }
}
