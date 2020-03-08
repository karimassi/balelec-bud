package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng DEFAULT_LOCATION = new LatLng(46.518802,6.567550);

    private LatLng position = DEFAULT_LOCATION;

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
        map.addMarker(new MarkerOptions().position(position).title("defaultPosition"));
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    public void setPosition(LatLng position) {
        if (position != null) {
            this.position = position;
        }
    }

    public LatLng getPosition() {
        return this.position;
    }
}