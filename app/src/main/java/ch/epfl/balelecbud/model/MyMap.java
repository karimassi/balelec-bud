package ch.epfl.balelecbud.model;

import com.mapbox.mapboxsdk.constants.MapboxConstants;

public interface MyMap {

    double DEFAULT_ZOOM = MapboxConstants.MINIMUM_ZOOM;

    double POI_ZOOM = MapboxConstants.MAXIMUM_ZOOM;

    void initialiseMap(boolean appLocationEnabled, Location defaultLocation, double zoom);

    MyMarker addMarker(MyMarker.Builder markerBuilder);

}
