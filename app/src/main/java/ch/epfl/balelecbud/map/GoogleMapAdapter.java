package ch.epfl.balelecbud.map;

import com.google.android.gms.maps.GoogleMap;

public class GoogleMapAdapter implements MyMap {
    private GoogleMap googleMap;
    public GoogleMapAdapter(GoogleMap googleMap) {
        this.googleMap = googleMap;
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
