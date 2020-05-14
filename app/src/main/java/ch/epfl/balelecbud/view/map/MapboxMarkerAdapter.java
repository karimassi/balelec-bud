package ch.epfl.balelecbud.view.map;


import com.mapbox.mapboxsdk.annotations.Marker;

import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.MyMarker;

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
