package ch.epfl.balelecbud;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.Emergency.EmergencyInfo;
import ch.epfl.balelecbud.Emergency.EmergencyNumbers;

public class EmergencyNumbersActivity extends BasicActivity {

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
        emergencyNumbersRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    EmergencyNumbers number = documentSnapshot.toObject(EmergencyNumbers.class);
                    repertoryMap.put(number.getName(), number.getNumber());
                }
                upddateListView();
            }
        });
    }


    public void fetchInfo(View view) {
        emergencyNumbersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    EmergencyNumbers number = documentSnapshot.toObject(EmergencyNumbers.class);
                    repertoryMap.put(number.getName(), number.getNumber());
                }
                upddateListView();
            }
        });

    }

    private void upddateListView() {
        List<String> numberList = new ArrayList<String>(Arrays.asList(repertoryMap.keySet().toArray(new String[0])));
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, numberList);
        numbersListView.setAdapter(arrayAdapter);

        numbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String entry = (String) parent.getAdapter().getItem(position);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + repertoryMap.get(entry)));
                startActivity(intent);

            }
        });
    }

}
