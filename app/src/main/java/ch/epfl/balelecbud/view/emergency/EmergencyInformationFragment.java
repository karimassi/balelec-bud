package ch.epfl.balelecbud.view.emergency;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.EmergencyInformation;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

public class EmergencyInformationFragment extends Fragment implements OnRecyclerViewInteractionListener<EmergencyInformation> {

    private boolean callPermissionGranted;
    public static final int PERMISSION_TO_CALL_CODE = 991;


    public static EmergencyInformationFragment newInstance() {
        return (new EmergencyInformationFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emergency_info, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_emergency_info);

        RecyclerView recyclerViewNumbers = getActivity().findViewById(R.id.recycler_view_emergency_numbers);
        recyclerViewNumbers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewNumbers.setHasFixedSize(true);

        RecyclerViewData<EmergencyInformation, EmergencyInformationHolder> numbersData = new EmergencyInformationData(true, this);
        RefreshableRecyclerViewAdapter<EmergencyInformation, EmergencyInformationHolder> numbersAdapter =
                new RefreshableRecyclerViewAdapter<>(EmergencyInformationHolder::new, numbersData, R.layout.item_emergency_info);
        recyclerViewNumbers.setAdapter(numbersAdapter);
        numbersAdapter.setOnRefreshListener(refreshLayout);

        RecyclerView recyclerViewInfo = getActivity().findViewById(R.id.recycler_view_emergency_info);
        recyclerViewInfo.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewInfo.setHasFixedSize(true);

        RecyclerViewData<EmergencyInformation, EmergencyInformationHolder> data = new EmergencyInformationData(false, null);
        RefreshableRecyclerViewAdapter<EmergencyInformation, EmergencyInformationHolder> adapter =
                new RefreshableRecyclerViewAdapter<>(EmergencyInformationHolder::new, data, R.layout.item_emergency_info);
        recyclerViewInfo.setAdapter(adapter);
        adapter.setOnRefreshListener(refreshLayout);

        FloatingActionButton fabDeclareEmergency = getView().findViewById(R.id.fab_declare_emergency);
        fabDeclareEmergency.setOnClickListener(v -> {
            SubmitEmergencyFragment dialog = SubmitEmergencyFragment.newInstance();
            dialog.show(getActivity().getSupportFragmentManager(), getString(R.string.declare_an_emergency));
        });

    }

    @Override
    public void onItemSelected(EmergencyInformation item) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getInstruction()));
        String[] permissions = {Manifest.permission.CALL_PHONE};

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            callPermissionGranted = false;
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    PERMISSION_TO_CALL_CODE);
            if (callPermissionGranted) {
                startActivity(intent);
            }
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_TO_CALL_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPermissionGranted = true;
                Log.w("Call permission", "allowed");
            } else {
                callPermissionGranted = false;
                Log.w("Call permission", "denied");
            }
        }
    }
}

