package ch.epfl.balelecbud;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.emergency.models.EmergencyNumber;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class EmergencyNumbersActivity extends BasicActivity {
    public static final int PERMISSION_TO_CALL_CODE = 991;
    private boolean callPermissionGranted;
    private ListView numbersListView;
    private ArrayAdapter arrayAdapter;
    private Map<String, String> repertoryMap = new HashMap<String, String>();
    List<String> numberList = new ArrayList<>(Arrays.asList(repertoryMap.keySet().toArray(new String[0])));




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BalelecbudApplication.getAppDatabaseWrapper();

        setContentView(R.layout.activity_emergency_numbers);
        configureToolBar(R.id.emergency_numbers_activity_toolbar);
        configureDrawerLayout(R.id.emergency_numbers_activity_drawer_layout);
        configureNavigationView(R.id.emergency_numbers_activity_nav_view);


        numbersListView = findViewById(R.id.numbersListView);
        arrayAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                numberList);
        numbersListView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BalelecbudApplication.getAppDatabaseWrapper().query( new MyQuery(DatabaseWrapper. EMERGENCY_NUMBER_PATH, new LinkedList<>()), EmergencyNumber.class).whenComplete((res, err) -> {
            for (EmergencyNumber number : res) {
                repertoryMap.put(number.getName(), number.getNumber());
            }
        });
            numberList.clear();
            numberList.addAll(new ArrayList<>(Arrays.asList(repertoryMap.keySet().toArray(new String[0]))));
            arrayAdapter.notifyDataSetChanged();
            makeListClickable();

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
