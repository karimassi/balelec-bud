package ch.epfl.balelecbud;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.emergency.models.EmergencyNumbers;

public class EmergencyNumbersActivity extends AppCompatActivity {
    public static final int PERMISSION_TO_CALL_CODE = 991;
    private boolean callPermissionGranted;
    private ListView numbersListView;
    private ArrayAdapter arrayAdapter;
    private Map<String, String> repertoryMap = new HashMap<String, String>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference emergencyNumbersRef = db.collection("emergencyNumbers");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_numbers);
        numbersListView = findViewById(R.id.numbersListView);
        arrayAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>());
    }

    @Override
    protected void onStart() {
        super.onStart();
        emergencyNumbersRef.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("EmergencyNumbers DB","empty collection");
                return;
            }
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                EmergencyNumbers number = documentSnapshot.toObject(EmergencyNumbers.class);
                repertoryMap.put(number.getName(), number.getNumber());
            }

            List<String> numberList = new ArrayList<>(Arrays.asList(repertoryMap.keySet().toArray(new String[0])));
            arrayAdapter = new ArrayAdapter(EmergencyNumbersActivity.this, android.R.layout.simple_list_item_1, numberList);
            numbersListView.setAdapter(arrayAdapter);

            makeListClickable();

        });
    }

    private void makeListClickable() {
        numbersListView.setOnItemClickListener((parent, view, position, id) -> {
            String entry = (String) parent.getAdapter().getItem(position);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + repertoryMap.get(entry)));

            String permissions[] = {Manifest.permission.CALL_PHONE};

            if(ActivityCompat.checkSelfPermission(EmergencyNumbersActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                callPermissionGranted = false;
                ActivityCompat.requestPermissions(EmergencyNumbersActivity.this,
                        permissions,
                        PERMISSION_TO_CALL_CODE);
                if(callPermissionGranted){
                    startActivity(intent);
                }
            }else{
                startActivity(intent);
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_CALL_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPermissionGranted = true;
                    Log.w("Call permission", "allowed");
                } else {
                    callPermissionGranted = false;
                    Log.w("Call permission", "denied");
                }
                return;
            }

        }
    }


}
