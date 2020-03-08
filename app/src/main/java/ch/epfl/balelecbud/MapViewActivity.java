package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.epfl.balelecbud.R;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng EPFL = new LatLng(46.518802,6.567550);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng currentPosition = EPFL;
        map.addMarker(new MarkerOptions().position(currentPosition).title("currentPosition"));
        map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
    }
}