package ch.epfl.balelecbud.map;

import com.google.android.gms.maps.model.Marker;

import ch.epfl.balelecbud.models.Location;

public class GoogleMarkerAdapter implements MyMarker {
    private Marker marker;

    public GoogleMarkerAdapter(Marker marker) {
        this.marker = marker;
    }

    @Override
    public void setLocation(Location location) {
        if (location != null) this.marker.setPosition(location.toLatLng());
    }
}
