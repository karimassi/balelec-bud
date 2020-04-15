package ch.epfl.balelecbud.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

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
        if (markerBuilder != null) {
            MarkerOptions markerOptions = markerBuilder.toGoogleMarkerOptions();
            return new GoogleMarkerAdapter(googleMap.addMarker(markerOptions));
        } else {
            return null;
        }
    }
}
