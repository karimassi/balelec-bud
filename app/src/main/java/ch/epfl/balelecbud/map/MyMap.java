package ch.epfl.balelecbud.map;

import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.Style;

public interface MyMap {

    void initialiseMap(boolean appLocationEnabled);
    MyMarker addMarker(MyMarker.Builder markerBuilder);


}
