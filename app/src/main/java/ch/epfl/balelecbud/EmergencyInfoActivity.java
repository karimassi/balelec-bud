package ch.epfl.balelecbud.Emergency;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ch.epfl.balelecbud.BasicActivity;
import ch.epfl.balelecbud.R;

public class EmergencyInfoActivity extends BasicActivity {

    private TextView textViewEmergencyInfo;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_info);
        textViewEmergencyInfo.setText("test");
        textViewEmergencyInfo = findViewById(R.id.ermergencyTextView);
    }

    @Override
    protected void onStart(){
        super.onStart();
        fetchInfo();
    }


    public void fetchInfo(){

        db.collection("emergencyInfo").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    String text = "";

                    for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        EmergencyInfo info = documentSnapshot.toObject(EmergencyInfo.class);
                        text += "Case: "+ info.getName()+"\nWhat to do: "+info.getInstruction()+"\n\n";

                    }
                    textViewEmergencyInfo.setText(text);
                }



            });

    }

}
