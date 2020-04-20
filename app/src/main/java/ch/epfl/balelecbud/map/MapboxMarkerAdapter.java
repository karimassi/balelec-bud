package ch.epfl.balelecbud.map;


import com.mapbox.mapboxsdk.annotations.Marker;

import ch.epfl.balelecbud.models.Location;

public class MapboxMarkerAdapter implements MyMarker {

    private Marker marker;

    public MapboxMarkerAdapter(Marker marker) {
        this.marker = marker;
    }

    @Override
    public void setLocation(Location location) {
        if (location != null) this.marker.setPosition(location.toLatLng());
    }
}
