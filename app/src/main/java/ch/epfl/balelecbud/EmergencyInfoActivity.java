package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import ch.epfl.balelecbud.Emergency.EmergencyInfo;

public class EmergencyInfoActivity extends BasicActivity {

    private TextView textViewEmergencyInfo;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference emergencyInfoRef = db.collection("emergencyInfo");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_info);
        textViewEmergencyInfo = findViewById(R.id.ermergencyTextView);
    }

    @Override
    protected void onStart(){
        super.onStart();
        emergencyInfoRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String text = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    EmergencyInfo info = documentSnapshot.toObject(EmergencyInfo.class);
                    text += "Case: "+ info.getName()+"\nWhat to do: "+info.getInstruction()+"\n\n";
                }

                textViewEmergencyInfo.setText(text);
            }
        });
    }


    public void fetchInfo(View view){
        emergencyInfoRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
