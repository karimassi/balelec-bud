package ch.epfl.balelecbud.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class GoogleMapAdapter implements OnMapReadyCallback, MyMap {
    private GoogleMap googleMap;
    public GoogleMapAdapter(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void setMyLocationEnabled(boolean locationEnabled) {
        this.googleMap.setMyLocationEnabled(locationEnabled);
    }

    @Override
    public MyMarker addMarker(MyMarker.Builder markerBuilder) {
        return new GoogleMarkerAdapter(googleMap.addMarker(markerBuilder.toGoogleMarkerOptions()));
    }
}
