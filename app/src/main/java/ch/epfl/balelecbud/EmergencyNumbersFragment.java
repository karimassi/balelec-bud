package ch.epfl.balelecbud;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.emergency.models.EmergencyNumber;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.Database;

public class EmergencyNumbersFragment extends Fragment {

    public static final int PERMISSION_TO_CALL_CODE = 991;
    private boolean callPermissionGranted;
    private ListView numbersListView;
    private ArrayAdapter arrayAdapter;
    private Map<String, String> repertoryMap = new HashMap<String, String>();
    List<String> numberList = new ArrayList<>(Arrays.asList(repertoryMap.keySet().toArray(new String[0])));

    public static EmergencyNumbersFragment newInstance() {
        return (new EmergencyNumbersFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_emergency_numbers, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BalelecbudApplication.getAppDatabase();
    }

    @Override
    public void onStart() {
        super.onStart();
        numbersListView = getActivity().findViewById(R.id.numbersListView);
        arrayAdapter = new ArrayAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                numberList);
        BalelecbudApplication.getAppDatabase().queryWithType( new MyQuery(Database. EMERGENCY_NUMBER_PATH, new LinkedList<>()), EmergencyNumber.class).whenComplete((res, err) -> {
            for (EmergencyNumber number : res) {
                repertoryMap.put(number.getName(), number.getNumber());
                Log.d("numer", number.getName());
            }
            numberList.clear();
            numberList.addAll(new ArrayList<>(Arrays.asList(repertoryMap.keySet().toArray(new String[0]))));
            arrayAdapter.notifyDataSetChanged();
            makeListClickable();

        });

    }


    private void makeListClickable() {
        numbersListView.setOnItemClickListener((parent, view, position, id) -> {
            String entry = (String) parent.getAdapter().getItem(position);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + repertoryMap.get(entry)));

            String permissions[] = {Manifest.permission.CALL_PHONE};

            if(ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                callPermissionGranted = false;
                ActivityCompat.requestPermissions(getActivity(),
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