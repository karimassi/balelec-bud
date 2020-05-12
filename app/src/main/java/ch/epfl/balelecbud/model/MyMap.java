package ch.epfl.balelecbud.model;

public interface MyMap {

    void initialiseMap(boolean appLocationEnabled, Location defaultLocation);

    MyMarker addMarker(MyMarker.Builder markerBuilder);

}
