package ch.epfl.balelecbud.Emergency;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ch.epfl.balelecbud.BasicActivity;
import ch.epfl.balelecbud.Emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.R;

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
        final String emergency_title = getResources().getString(R.string.emergency_tile);
        final String emergency_solution = getResources().getString(R.string.emergency_solution);

        emergencyInfoRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("EmergencyInfo DB","empty collection");
                    return;
                }
                StringBuilder text = new StringBuilder();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    EmergencyInfo info = documentSnapshot.toObject(EmergencyInfo.class);
                    text.append(emergency_title+ info.getName()+"\n"+emergency_solution+info.getInstruction()+"\n\n");
                }

                textViewEmergencyInfo.setText(text);
            }
        });
    }


}
