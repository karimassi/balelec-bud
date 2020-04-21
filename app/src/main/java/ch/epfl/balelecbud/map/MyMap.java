package ch.epfl.balelecbud.map;

import ch.epfl.balelecbud.models.Location;

public interface MyMap {

    void initialiseMap(boolean appLocationEnabled, Location defaultLocation);

    MyMarker addMarker(MyMarker.Builder markerBuilder);

}
