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

    private double defaultLat;
    private double defaultLng;

    private LatLng position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        defaultLat = Double.parseDouble(getString(R.string.default_lat));
        defaultLng = Double.parseDouble(getString(R.string.default_lng));
        LatLng default_location = new LatLng(defaultLat,defaultLng);

        setPosition(default_location);

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